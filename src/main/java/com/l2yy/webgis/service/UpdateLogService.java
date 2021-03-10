package com.l2yy.webgis.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.UpdateLogDTO;
import com.l2yy.webgis.entity.UpdateLogDO;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author huangjiale
 * @since 2020-03-27
 */
public interface UpdateLogService extends IService<UpdateLogDO> {

    List<UpdateLogDTO> getLogList(Boolean reverse);

    Boolean addLog(String msg);

}
