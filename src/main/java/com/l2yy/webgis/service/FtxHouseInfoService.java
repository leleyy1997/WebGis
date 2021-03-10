package com.l2yy.webgis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.HeatMapDTO;
import com.l2yy.webgis.dto.HeatMapDataDTO;
import com.l2yy.webgis.dto.QueryListDTO;
import com.l2yy.webgis.entity.FtxHouseInfoDO;
import com.l2yy.webgis.util.PageableResultVO;

import java.util.List;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/15 5:06 下午
 * @description：
 */
public interface FtxHouseInfoService extends IService<FtxHouseInfoDO> {

    int getFtxHouseInfoList(String city);

    PageableResultVO<FtxHouseInfoDO> houseInfoList(QueryListDTO<FtxHouseInfoDO> queryListDTO);

    List<HeatMapDTO> getHeatMapData(HeatMapDataDTO heatMapDataDTO);
}
