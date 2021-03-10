package com.l2yy.webgis.controller;

import com.l2yy.webgis.common.BaseController;
import com.l2yy.webgis.entity.FtxCityListDO;
import com.l2yy.webgis.entity.FtxHouseInfoDO;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import com.l2yy.webgis.service.FtxCityListService;
import com.l2yy.webgis.service.FtxHouseInfoService;
import com.l2yy.webgis.service.IHandleExcelService;
import com.l2yy.webgis.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/31 11:33 下午
 * @description：
 */
@RestController
@RequestMapping("export")
public class ExcelController extends BaseController {

    @Autowired
    IHandleExcelService excelService;

    @Autowired
    private FtxHouseInfoService houseInfoService;

    @Autowired
    private FtxCityListService ftxCityListService;

//    @TokenCheck
    @GetMapping("/excel")
    public CommonResponse export(Long cityId) {

        FtxCityListDO one = ftxCityListService.lambdaQuery().eq(FtxCityListDO::getId, cityId).one();
        if(one.getHaveData() == 0){
            throw new BizException(CommonBizCodeEnum.EXCEL_EXPORT_NO_DATA);
        }

        List<FtxHouseInfoDO> list = houseInfoService.lambdaQuery().eq(FtxHouseInfoDO::getCityId, cityId).list();
        try {
            LinkedHashMap<String, String> hMap = new LinkedHashMap<>();
            hMap.put("name", "名称");
            hMap.put("price", "价格");
            hMap.put("address", "地址");
            hMap.put("url", "小区图片");
            hMap.put("units", "价格单位");
            hMap.put("lon", "lon");
            hMap.put("lat", "lat");
            hMap.put("phone", "电话");
            hMap.put("mainType","主力户型");
            excelService.export(one.getName()+"市房天下数据", hMap, list, getHttpServletResponse());
            return CommonResponse.buildSuccess();
        } catch (BizException e) {
            return CommonResponse.buildFail(e.getBizCode());
        }
    }

}
