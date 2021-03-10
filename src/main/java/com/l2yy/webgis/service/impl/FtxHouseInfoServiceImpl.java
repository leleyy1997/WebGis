package com.l2yy.webgis.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.common.WebSocket;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.HeatMapDTO;
import com.l2yy.webgis.dto.HeatMapDataDTO;
import com.l2yy.webgis.dto.ProgressRateDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.FtxCityListDO;
import com.l2yy.webgis.entity.FtxHouseInfoDO;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import com.l2yy.webgis.mapper.FtxHouseInfoMapper;
import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.util.PageableResultVO;
import com.l2yy.webgis.util.userAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 5:06 下午
 * @description：
 */
@Service
public class FtxHouseInfoServiceImpl extends ServiceImpl<FtxHouseInfoMapper, FtxHouseInfoDO> implements FtxHouseInfoService {

    private static final Executor EXECUTOR = new ThreadPoolExecutor(10, 15, 5L, TimeUnit.MINUTES, new ArrayBlockingQueue<>(100));

    private Logger logger = LoggerFactory.getLogger(FtxHouseInfoServiceImpl.class);

    @Autowired
    private WebSocket webSocket;

    @Autowired
    private FtxCityListService ftxCityListService;

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public int getFtxHouseInfoList(String city) {

        FtxCityListDO keyCity = ftxCityListService.lambdaQuery().eq(FtxCityListDO::getName, city).last("LIMIT 1").one();
        if (keyCity == null || keyCity.equals("")) {
            return CommonConstant.NO_CITY;
        }
        if (keyCity.getHaveData() == CommonConstant.HAVE_DATA) {
            return CommonConstant.EXIST_DATA;
        }
        String url = keyCity.getUrl();
        Long total = initFtxHouse(url);
        if (total == 1L) {
            return CommonConstant.NO_DATA;
        }

        int totalPage = (int) Math.ceil((double) total.intValue() / CommonConstant.FTX_PAGE_SIZE);

        AtomicInteger current = new AtomicInteger();

//        AtomicBoolean b = new AtomicBoolean(false);

        for (int i = 1; i <= totalPage; i++) {

            int finalI = i;
            EXECUTOR.execute(() -> {
                Document document = null;
                try {
                    document = Jsoup.connect("https://" + getKey(url) + ".newhouse.fang.com/house/s/b9" + finalI + "/?ctm=1.sh.xf_search.list_type.9").userAgent(userAgent.getMyUserAgent()).timeout(10000).get();
                    if (document == null) {
                        return;
                    }
                    logger.info("当前第{}页", finalI);
                    List<FtxHouseInfoDO> ftxHouseList = new ArrayList<>();
                    Elements elements = document.select("#newhouse_loupai_list > ul > li");
                    for (Element element : elements) {
                        current.getAndIncrement();
                        ProgressRateDTO progressRateDTO = new ProgressRateDTO();
                        progressRateDTO.setTotal(total);
                        progressRateDTO.setCurrent(current.get());
                        progressRateDTO.setTime(LocalDateTime.now());
                        String s = JSON.toJSONString(progressRateDTO);
                        webSocket.sendMessage(String.valueOf(11), s);
                        String name = element.select("div.clearfix > div.nlc_details > div.house_value.clearfix > div.nlcd_name > a").text();
                        if (StringUtils.isEmpty(name)) {
                            current.getAndDecrement();
                            continue;
                        }
                        String price = element.select("div.clearfix > div.nlc_details > div.nhouse_price > span").text();
                        String units = element.select("div.clearfix > div.nlc_details > div.nhouse_price > em").text();
                        if (StringUtils.isEmpty(price)) {
                            price = element.select("div.clearfix > div.nlc_details > div.nhouse_price").text();
                            if (price.contains("/")) {
                                int a = price.indexOf("/");
                                price = price.replace(price.substring(a), "/平方米");
                            }
                            units = "";
                        }

                        String address = element.select("div.clearfix > div.nlc_details > div.relative_message.clearfix > div.address > a").text();
                        String status = element.select("div.clearfix > div.nlc_details > div.fangyuan pr").text();
//                    String phone = element.select("div.clearfix > div.nlc_details > div.relative_message.clearfix > div.tel > p").text();
                        String mainType = element.select("div.clearfix > div.nlc_details > div.house_type.clearfix").text();
//                    String src = "https:" + element.select("div.clearfix > div.nlc_img > a > img:nth-child(2)").attr("src");
                        String baseUrl = element.select("div.clearfix > div.nlc_img > a").attr("href");
                        if (baseUrl.substring(0, 1).equals("/")) {
                            baseUrl = "https:" + baseUrl;
                        }

                        Document document1 = Jsoup.connect(baseUrl).userAgent(userAgent.getMyUserAgent()).timeout(20000).get();
                        String infoSrc = document1.select("div.mapbox > div.mapbox_dt > iframe").attr("src");
                        if (StringUtils.isEmpty(infoSrc)) {
                            continue;
                        }
                        String infoUrl = "http:" + infoSrc;
                        logger.info(infoUrl);

                        Document document2 = Jsoup.connect(infoUrl).userAgent(userAgent.getUserAgent()).timeout(5000).get();
                        Elements body = document2.select("body");
                        String script = body.get(0).selectFirst("script").data();
                        String replace = script.replace("var mainBuilding=", "").replace(";", "");
                        JSONObject jsonObject = JSONObject.parseObject(replace);
                        Double lon = jsonObject.getDouble("baidu_coord_x");
                        Double lat = jsonObject.getDouble("baidu_coord_y");
//                        Double score = jsonObject.getDouble("zongfen");
                        Double score = 4.0;
                        String title = jsonObject.getString("title");
//                    String address = jsonObject.getString("address");
                        if (!StringUtils.isEmpty(units)) {
                            units = jsonObject.getString("price_unit");
                        }
                        String src = "https:" + jsonObject.getString("picAddress");
                        String phone = jsonObject.getString("tel");

                        logger.info("当前房源：[{}]", name);

                        FtxHouseInfoDO ftx = FtxHouseInfoDO.builder()
                                .address(address)
                                .status(status)
                                .phone(phone)
                                .lat(lat)
                                .lon(lon)
                                .name(title)
                                .price(price)
                                .units(units)
                                .score(score)
                                .mainType(mainType)
                                .url(src)
                                .cityId(keyCity.getId())
                                .build();
                        ftxHouseList.add(ftx);
                    }
                    saveBatch(ftxHouseList);
                } catch (IOException e) {
                    logger.error("PC出错", e);
                }
            });
        }
        ftxCityListService.lambdaUpdate().eq(FtxCityListDO::getId,keyCity.getId()).set(FtxCityListDO::getHaveData,CommonConstant.HAVE_DATA).update();
//        ftxCityListService.updateById(FtxCityListDO.builder()
//                .id(keyCity.getId())
//                .haveData(CommonConstant.HAVE_DATA)
//                .build());
        return current.get();
    }

    @Override
    public PageableResultVO<FtxHouseInfoDO> houseInfoList(QueryListDTO<FtxHouseInfoDO> queryListDTO) {
        LambdaQueryWrapper<FtxHouseInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (queryListDTO.getData() != null) {
            if (!queryListDTO.getData().getName().isEmpty()) {
                lambdaQueryWrapper.like(FtxHouseInfoDO::getName, queryListDTO.getData().getName());
            }
            if (queryListDTO.getData().getCityId() != null) {
                lambdaQueryWrapper.eq(FtxHouseInfoDO::getCityId, queryListDTO.getData().getCityId());
            }
        }
        IPage<FtxHouseInfoDO> page = new Page<>();
        page.setCurrent(queryListDTO.getPage());
        page.setSize(queryListDTO.getPageSize());

        IPage<FtxHouseInfoDO> houseInfoPage = this.page(page, lambdaQueryWrapper);
        List<FtxHouseInfoDO> records = houseInfoPage.getRecords();
        return new PageableResultVO<>(true, houseInfoPage.getTotal(), records);
    }

    @Override
    public List<HeatMapDTO> getHeatMapData(HeatMapDataDTO heatMapDataDTO) {
        if (heatMapDataDTO.getType() == CommonConstant.AJK_DATA) {
            throw new BizException(CommonBizCodeEnum.NOT_SUPPORT_TYPE);
        }
        if ("".equals(heatMapDataDTO.getCityId())){
            throw new BizException(CommonBizCodeEnum.NOT_SELECT_CITY);
        }
        LambdaQueryWrapper<FtxHouseInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (heatMapDataDTO.getCityId() == null || "".equals(heatMapDataDTO.getCityId())) {
            lambdaQueryWrapper.eq(FtxHouseInfoDO::getCityId, 84);
        } else {
            lambdaQueryWrapper.eq(FtxHouseInfoDO::getCityId, heatMapDataDTO.getCityId());
        }
        return list(lambdaQueryWrapper).stream().map(a -> {
            HeatMapDTO heatMapDTO = new HeatMapDTO();
            heatMapDTO.setCount(a.getPrice());
            heatMapDTO.setLat(a.getLat());
            heatMapDTO.setLng(a.getLon());
            return heatMapDTO;
        }).collect(Collectors.toList());
    }

    public Long initFtxHouse(String url) {
//        http:sh.fang.com/.
//        https://sh.newhouse.fang.com/house/s/b91/
        String key = getKey(url);
        try {
            Document document = Jsoup.connect("https://" + key + ".newhouse.fang.com/house/s/b91/?ctm=1.sh.xf_search.list_type.9").userAgent(userAgent.getMyUserAgent()).timeout(10000).get();
//            String total = document.select("div.page > ul > li.fl > b").text();
            String total = document.select("div.nav_f_box > div > ul > li  > #allUrl > span").text();
            total = total.replace("(", "").replace(")", "");
            if (StringUtils.isEmpty(total)) {
                return (long) CommonConstant.NO_DATA;
            }
            ProgressRateDTO progressRateDTO = new ProgressRateDTO();
            progressRateDTO.setTotal(Long.valueOf(total));
            progressRateDTO.setCurrent(0L);
            progressRateDTO.setTime(LocalDateTime.now());

            String s = JSON.toJSONString(progressRateDTO);
            webSocket.sendMessage(String.valueOf(11), s);
            return Long.valueOf(total);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return (long) CommonConstant.NO_DATA;
    }

    public String getKey(String url) {
        url = "http:" + url;
        Pattern p = Pattern.compile(":[\\w-]+");
        Matcher m = p.matcher(url);
        String key = "";
        while (m.find()) {
            String group = m.group(0);
            key = group.replace(":", "");
        }
        return key;
    }
}
