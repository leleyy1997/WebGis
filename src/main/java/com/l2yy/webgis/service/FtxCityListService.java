package com.l2yy.webgis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.CityListDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.FtxCityListDO;
import com.l2yy.webgis.util.PageableResultVO;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 1:52 下午
 * @description：
 */
public interface FtxCityListService extends IService<FtxCityListDO> {

    void getFtxCityList();

    PageableResultVO<CityListDTO> FtxCityList(QueryListDTO<CityListDTO> queryListDTO);

    Boolean delCityData(Long cityId);
}
