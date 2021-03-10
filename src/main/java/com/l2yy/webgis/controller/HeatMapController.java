package com.l2yy.webgis.controller;

import com.l2yy.webgis.dto.HeatMapDTO;
import com.l2yy.webgis.dto.HeatMapDataDTO;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author ：hjl
 * @date ：Created in 2020/5/6 1:03 下午
 * @description：热力图 controller
 */
@RestController
@RequestMapping("/heat-map")
public class HeatMapController {

    @Autowired
    FtxHouseInfoService ftxHouseInfoService;

    @PostMapping("/getData")
    public CommonResponse<List<HeatMapDTO>> HeatMap(@RequestBody HeatMapDataDTO heatMapDataDTO) {
        return CommonResponse.buildSuccess(ftxHouseInfoService.getHeatMapData(heatMapDataDTO));
    }
}
