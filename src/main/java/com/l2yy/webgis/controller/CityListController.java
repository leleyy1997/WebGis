package com.l2yy.webgis.controller;

import com.l2yy.webgis.annotation.TokenCheck;
import com.l2yy.webgis.dto.CityListDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.CityListDO;
import com.l2yy.webgis.service.CityListService;
import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.util.CommonResponse;
import com.l2yy.webgis.util.PageableResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/3 8:28 下午
 * @description：城市列表list
 */
@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/house")
public class CityListController {

    @Autowired
    private CityListService ajkHouseService;

    @Autowired
    private FtxCityListService ftxCityListService;

    /**
     * 安居客城市列表初识化，暂时根据静态htm
     * @return
     */
    @GetMapping("/city")
    public Long getCityList(){
        return ajkHouseService.getCityList();
    }

    /**
     * 房天下城市列表初始化 实时
     */
    @GetMapping("/ftx-city")
    public void getFtxCityList(){
        ftxCityListService.getFtxCityList();
    }

    /**
     * 安居客城市列表 包含搜索
     * @param queryListDTO
     * @return
     */
    @TokenCheck
    @PostMapping("/city-list")
    public CommonResponse<PageableResultVO<CityListDO>> CityList(@RequestBody QueryListDTO<CityListDO> queryListDTO){
        return CommonResponse.buildSuccess(ajkHouseService.CityList(queryListDTO));
    }

    /**
     * 房天下城市列表 包含搜索
     * @param queryListDTO
     * @return
     */
    @TokenCheck
    @PostMapping("/ftx-city-list")
    public CommonResponse<PageableResultVO<CityListDTO>> FtxCityList(@RequestBody QueryListDTO<CityListDTO> queryListDTO){
        return CommonResponse.buildSuccess(ftxCityListService.FtxCityList(queryListDTO));
    }

    @TokenCheck
    @GetMapping("/del-ftx-data")
    public CommonResponse<Boolean> DelFtxData(@RequestParam("cityId") Long cityId){
        return CommonResponse.buildSuccess(ftxCityListService.delCityData(cityId));
    }

}
