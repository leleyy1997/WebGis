package com.l2yy.webgis.controller;

import com.l2yy.webgis.annotation.TokenCheck;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.FtxHouseInfoDO;
import com.l2yy.webgis.entity.HouseInfoDO;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.service.HouseInfoService;
import com.l2yy.webgis.util.CommonResponse;
import com.l2yy.webgis.util.PageableResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/11 12:10 上午
 * @description：房价信息 controller
 */
@RestController
@RequestMapping("/city")
public class HouseInfoController {

    @Autowired
    private HouseInfoService houseInfoService;

    @Autowired
    private FtxHouseInfoService ftxHouseInfoService;


    @TokenCheck
    @PostMapping("/house-info")
    public CommonResponse<PageableResultVO<HouseInfoDO>> CityList(@RequestBody QueryListDTO<HouseInfoDO> queryListDTO){
        return CommonResponse.buildSuccess(houseInfoService.houseInfoList(queryListDTO));
    }

    @TokenCheck
    @PostMapping("/ftx-house-info")
    public CommonResponse<PageableResultVO<FtxHouseInfoDO>> FtxCityList(@RequestBody QueryListDTO<FtxHouseInfoDO> queryListDTO){
        return CommonResponse.buildSuccess(ftxHouseInfoService.houseInfoList(queryListDTO));
    }


}
