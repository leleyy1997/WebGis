package com.l2yy.webgis.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.CityListDO;
import com.l2yy.webgis.mapper.CityListMapper;
import com.l2yy.webgis.service.CityListService;
import com.l2yy.webgis.util.PageableResultVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/3 9:28 下午
 * @description：
 */
@Service
public class CityListServiceImpl extends ServiceImpl<CityListMapper, CityListDO> implements CityListService {

    private final static String PATTERN = "/[a-zA-Z]+";

    @Override
    @Transactional(rollbackFor = {RuntimeException.class, Error.class})
    public Long getCityList() {
        InputStream resourceAsStream = this.getClass().getResourceAsStream("/static/ajk.htm");


//        File input = new File("/static/ajk.htm");
        Document document = null;
        try {
            document = Jsoup.parse(resourceAsStream, "UTF-8","");
            Elements elements = document.select("div.content > div.city-itm> div.letter_city > ul > li >div.city_list > a");
            for (Element element : elements) {
                CityListDO cityListDO = new CityListDO();
                String url = element.select("a").attr("href");
                Pattern p = Pattern.compile(PATTERN);
                Matcher matcher = p.matcher(url);
                while (matcher.find()) {
                    String s = matcher.group(0);
                    String key = s.substring(1);
                    cityListDO.setKeyword(key);
                }
                String city = element.select("a").text();
                if (element.select("a").attr("class").equals("hot")) {
                    cityListDO.setHot(CommonConstant.HOT);
                } else {
                    cityListDO.setHot(CommonConstant.NOT_HOT);
                }
                cityListDO.setCity(city);
                cityListDO.setUrl(url);
                save(cityListDO);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public PageableResultVO<CityListDO> CityList(QueryListDTO<CityListDO> queryListDTO) {
        LambdaQueryWrapper<CityListDO> lambdaQueryWrapper = new LambdaQueryWrapper();
        if (queryListDTO.getData() != null){
            if (queryListDTO.getData().getCity() != null && !StringUtils.trimAllWhitespace(queryListDTO.getData().getCity()).isEmpty()) {
                lambdaQueryWrapper.like(CityListDO::getCity,StringUtils.trimAllWhitespace(queryListDTO.getData().getCity()));
            }
        }
        IPage<CityListDO> page = new Page<>();
        page.setCurrent(queryListDTO.getPage());
        page.setSize(queryListDTO.getPageSize());

        IPage<CityListDO> cityPage = this.page(page, lambdaQueryWrapper);

        List<CityListDO> collect = cityPage.getRecords();


        return new PageableResultVO<>(true, cityPage.getTotal(), collect);
    }
}
