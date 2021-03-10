package com.l2yy.webgis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.CityListDO;
import com.l2yy.webgis.util.PageableResultVO;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/3 9:27 下午
 * @description：
 */
public interface CityListService extends IService<CityListDO> {


    Long getCityList();

    PageableResultVO<CityListDO> CityList(QueryListDTO<CityListDO> queryListDTO);
}
