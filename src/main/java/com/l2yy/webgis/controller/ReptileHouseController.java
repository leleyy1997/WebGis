package com.l2yy.webgis.controller;

import com.l2yy.webgis.annotation.TokenCheck;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.service.HouseInfoService;
import com.l2yy.webgis.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/3 8:32 下午
 * @description：爬取数据 controller
 */
@RestController
@RequestMapping("/reptile")
public class ReptileHouseController {

    @Autowired
    private HouseInfoService houseInfoService;

    @Autowired
    private FtxHouseInfoService ftxHouseInfoService;


    /**
     * 安居客爬虫入口
     * @param city
     * @return
     */
    @TokenCheck
    @GetMapping("/city")
    public CommonResponse<Integer> reptileHouse(@RequestParam("city") String city) {
        return CommonResponse.buildSuccess(houseInfoService.getHouseInfo(city));
    }

    /**
     * 方天下爬虫入口
     * @param city
     * @return
     */
    @TokenCheck
    @GetMapping("/ftxCity")
    public CommonResponse<Integer> reptileFtxHouse(@RequestParam("city") String city) {
        return CommonResponse.buildSuccess(ftxHouseInfoService.getFtxHouseInfoList(city));
    }
}
