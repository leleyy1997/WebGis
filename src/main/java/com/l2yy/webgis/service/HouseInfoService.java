package com.l2yy.webgis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.HouseInfoDO;
import com.l2yy.webgis.util.PageableResultVO;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/7 5:08 下午
 * @description：安居客房价信息
 */
public interface HouseInfoService extends IService<HouseInfoDO> {

    int getHouseInfo(String city);

    PageableResultVO<HouseInfoDO> houseInfoList(QueryListDTO<HouseInfoDO> queryListDTO);

}
