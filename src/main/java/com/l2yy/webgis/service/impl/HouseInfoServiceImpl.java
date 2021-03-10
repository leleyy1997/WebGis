package com.l2yy.webgis.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.common.WebSocket;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.ProgressRateDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.CityListDO;
import com.l2yy.webgis.entity.HouseInfoDO;
import com.l2yy.webgis.mapper.HouseInfoMapper;
import com.l2yy.webgis.service.CityListService;
import com.l2yy.webgis.service.HouseInfoService;
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

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/7 5:09 下午
 * @description：
 */
@Service
public class HouseInfoServiceImpl extends ServiceImpl<HouseInfoMapper, HouseInfoDO> implements HouseInfoService {

    private static final Executor EXECUTOR= new ThreadPoolExecutor(10,15,5L, TimeUnit.MINUTES,new ArrayBlockingQueue<>(100));

    private Logger logger = LoggerFactory.getLogger(HouseInfoServiceImpl.class);

    @Autowired
    private CityListService cityListService;

    @Autowired
    private WebSocket webSocket;


    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public int getHouseInfo(String city) {

        CityListDO keyCity = cityListService.lambdaQuery().eq(CityListDO::getCity, city).last("LIMIT 1").one();
        if (keyCity == null) {
            return 0;
        }
        if (keyCity.getHaveData() == CommonConstant.HAVE_DATA) {
            return 2;
        }
        String keyword = keyCity.getKeyword();

        Long total = initHouse(keyword);// 发送总条数
        if (total == 1L) {
            return 1;
        }

        int totalPage = (int) Math.ceil((double) total.intValue() / CommonConstant.PAGE_SIZE);

        logger.info(String.valueOf(totalPage));
        int current = 0;
        boolean b = false;

        for (int i = 1; i <= totalPage; i++) {

            Document document = null;
            try {
                document = Jsoup.connect("https://" + keyword + ".fang.anjuke.com/loupan/all/p" + i + "/").userAgent(userAgent.getUserAgent()).timeout(10000).referrer("https://beijing.anjuke.com/").headers(initHeader()).get();
                if (document == null) {
                    return 1;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("当前第" + i + "页开始");

            List<HouseInfoDO> anjukes = new ArrayList<>();
            Elements elements = document.select("div.key-list> div.item-mod");
            for (Element element : elements) {
                current++;
                HouseInfoDO anjuke = new HouseInfoDO();
                String name = element.select("div.infos > a.lp-name > span.items-name").text();
                if (name.equals("")) {
                    current--;
                    continue;
                }
                String src = element.select("a.pic > img").attr("src");
                String price = element.select("a.favor-pos > p.price > span").text();
                if (price.equals("")) {
                    price = element.select("a.favor-pos > p.price-txt").text();
                }
                String address2 = element.select("a.address > span.list-map").text();
                String address1 = address2.replaceAll("[\\[][\\W\\w]+[\\]]", "");
                String address = address1.replaceAll("[（](.*)[）]?", "");

                String url = element.select("a.pic").attr("href");
                Document document1 = null;
                try {
                    document1 = Jsoup.connect(url).userAgent(userAgent.getUserAgent()).timeout(5000).referrer("https://beijing.anjuke.com/").headers(initHeader()).get();
                    if (document1 == null) {
                        return 1;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String units = document1.select("dd.price > p > span").text();
                Pattern p = Pattern.compile("lat: [0-9]+.[0-9]+");
                Matcher m = p.matcher(document1.toString());
                while (m.find()) {
                    String a = m.group(0);
                    int j = a.indexOf(":");
                    String substring = a.substring(j + 1);
                    anjuke.setLat(Double.parseDouble(substring));
                }
                Pattern p1 = Pattern.compile("lng: [0-9]+.[0-9]+");
                Matcher m1 = p1.matcher(document1.toString());
                while (m1.find()) {
                    String a = m1.group(0);
                    int j = a.indexOf(":");
                    String substring = a.substring(j + 1);
                    anjuke.setLon(Double.parseDouble(substring));
                }
                System.out.println(name);

                ProgressRateDTO progressRateDTO = new ProgressRateDTO();
                progressRateDTO.setTotal(total);
                progressRateDTO.setCurrent((long) current);
                progressRateDTO.setTime(LocalDateTime.now());
                String s = JSON.toJSONString(progressRateDTO);
                webSocket.sendMessage(String.valueOf(11), s);

                anjuke.setName(name);
                anjuke.setUrl(src);
                anjuke.setPrice(price);
                anjuke.setAddress(address);
                anjuke.setUnits(units);
                anjuke.setCityId(keyCity.getId());
                anjukes.add(anjuke);

            }

            b = this.saveBatch(anjukes);

        }
        if (b)
            cityListService.updateById(CityListDO.builder()
                    .id(keyCity.getId())
                    .haveData(CommonConstant.HAVE_DATA).build());
        return current;

    }

    @Override
    public PageableResultVO<HouseInfoDO> houseInfoList(QueryListDTO<HouseInfoDO> queryListDTO) {
        LambdaQueryWrapper<HouseInfoDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        if (queryListDTO.getData() != null) {
            if (!queryListDTO.getData().getName().isEmpty()) {
                lambdaQueryWrapper.like(HouseInfoDO::getName, queryListDTO.getData().getName());
            }
            if (queryListDTO.getData().getCityId() != null) {
                lambdaQueryWrapper.eq(HouseInfoDO::getCityId, queryListDTO.getData().getCityId());
            }
        }
        IPage<HouseInfoDO> page = new Page<>();
        page.setCurrent(queryListDTO.getPage());
        page.setSize(queryListDTO.getPageSize());

        IPage<HouseInfoDO> houseInfoPage = this.page(page, lambdaQueryWrapper);
        List<HouseInfoDO> records = houseInfoPage.getRecords();
        return new PageableResultVO<>(true, houseInfoPage.getTotal(), records);
    }

    public HashMap<String, String> initHeader() {
        HashMap<String, String> map = new HashMap();
        map.put("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
        map.put("accept-encoding", "gzip, deflate, br");
        map.put("accept-language", "zh-CN,zh;q=0.9");
        map.put("cache-control", "max-age=0");
        map.put("sec-fetch-dest", "document");
        map.put("sec-fetch-mode", "navigate");
        map.put("sec-fetch-user", "?1");
//        map.put("cookie","sessid=9852B823-F2D6-1A2A-B237-5DEE3CECBE6A; aQQ_ajkguid=0B3D06FE-711E-AA20-0CB6-A50546B63204; _ga=GA1.2.1677223712.1583144039; 58tj_uuid=bdd8f429-89ef-4192-904a-655b5bbdc3d4; als=0; isp=true; lps=http%3A%2F%2Fuser.anjuke.com%2Fajax%2FcheckMenu%2F%3Fr%3D0.8259641108468738%26callback%3DjQuery111308564866833586608_1583648641072%26_%3D1583648641073%7Chttps%3A%2F%2Fbj.fang.anjuke.com%2F%3Ffrom%3Dnavigation; twe=2; Hm_lvt_c5899c8768ebee272710c9c5f365a6d8=1583144049,1583648641; _gid=GA1.2.273964561.1584027493; wmda_uuid=adda71bcce70511466f61a43814ff2d1; wmda_new_uuid=1; wmda_visited_projects=%3B8788302075828; isp=true; init_refer=; new_uv=21; new_session=0; wmda_session_id_8788302075828=1584101247617-4b5e9cd3-0ae1-fe08; ctid=63; Hm_lpvt_c5899c8768ebee272710c9c5f365a6d8=1584101723");
        return map;
    }

    public Long initHouse(String city) {
        Document document = null;
        Long total = 0L;
        try {
            logger.info(city);
            logger.info("https://" + city + ".fang.anjuke.com/loupan/all/p1/");

//            String url = "https://" + city + ".fang.anjuke.com/loupan/all/p1/";
//            System.out.println("访问地址:" + url);
//            URL serverUrl = new URL(url);
//            HttpURLConnection conn = (HttpURLConnection) serverUrl.openConnection();
//            conn.setRequestMethod("GET");
//            conn.setRequestProperty("accept", "*/*");
//            conn.setRequestProperty("connection", "Keep-Alive");
//            // 必须设置false，否则会自动redirect到Location的地址
//            conn.setInstanceFollowRedirects(false);
//            //获取Location地址
//            String location = conn.getHeaderField("Location");
//            System.out.println(location);
//            Connection.Response execute = Jsoup.connect("http://" + city + ".fang.anjuke.com/loupan/all/p1/").followRedirects(false).execute();
//            logger.info("code:{} , loca:{}",execute.statusCode(),execute.header("Location"));

            document = Jsoup.connect("https://" + city + ".fang.anjuke.com/loupan/all/p1/").userAgent(userAgent.getUserAgent()).timeout(10000).referrer("https://beijing.anjuke.com/").headers(initHeader()).get();
            if (document == null) {
                return 1L;
            }
            total = Long.valueOf(document.select("#container > div.list-contents > div.list-results > div.key-sort > div.sort-condi > span > em").text());

            ProgressRateDTO progressRateDTO = new ProgressRateDTO();
            progressRateDTO.setTotal(total);
            progressRateDTO.setCurrent(0L);
            progressRateDTO.setTime(LocalDateTime.now());

            String s = JSON.toJSONString(progressRateDTO);
            webSocket.sendMessage(String.valueOf(11), s);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }
}
