package com.l2yy.webgis;

import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.util.userAgent;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 2:15 下午
 * @description：
 */
@SpringBootTest
public class houseTest {

    @Autowired
    FtxCityListService ftxCityListService;

    @Autowired
    FtxHouseInfoService houseInfoService;

    @Test
    public void test() throws Exception {
        ftxCityListService.getFtxCityList();
    }

    @Test
    public void test2(){
        houseInfoService.getFtxHouseInfoList("上海");
    }

    @Test
    public void test1() throws IOException {
        Document document = Jsoup.connect("https://sh.newhouse.fang.com/house/s/b92/?ctm=1.sh.xf_search.list_type.8").userAgent(userAgent.getUserAgent()).timeout(10000).get();
        Elements elements = document.select("#newhouse_loupai_list > ul");
//        String src = elements.select("div.clearfix > div.nlc_img > a > img:nth-child(2)").attr("src");
        String name = elements.select("div.clearfix > div.nlc_details > div.house_value.clearfix > div.nlcd_name > a").text();
        String units = elements.select("div.clearfix > div.nlc_details > div.nhouse_price > em").text();
        System.out.println(units);

    }

    @Test
    public void test3() throws IOException {
        Document document = Jsoup.connect("https://131340.fang.com/?ctm=1.sh.xf_search.lplist.4").userAgent(userAgent.getUserAgent()).timeout(10000).get();

        String url = document.select("div.mapbox > div.mapbox_dt > iframe").attr("src");
//        System.out.println(url);
        String infoUrl ="http:"+ document.select("div.mapbox > div.mapbox_dt > iframe").attr("src");
        System.out.println(infoUrl);
//        Document document1 = Jsoup.connect(infoUrl).get();
//        Elements body = document1.select("body");
//        String script = body.get(0).selectFirst("script").data();
//        String replace = script.replace("var mainBuilding=", "").replace(";","");
//        System.out.println(JSONObject.parseObject(replace).getDouble("baidu_coord_x"));



    }

    @Test
    public void aVoid (){
        String a = "123/m";
        if (a.contains("/")){
            int i = a.indexOf("/");
            a = a.replace(a.substring(i),"/平方米");
            System.out.println(a);


        }
        System.out.println(a);


    }
}
