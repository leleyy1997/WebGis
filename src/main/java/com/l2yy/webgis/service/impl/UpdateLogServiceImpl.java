package com.l2yy.webgis.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.UpdateLogDTO;
import com.l2yy.webgis.entity.UpdateLogDO;
import com.l2yy.webgis.mapper.UpdateLogMapper;
import com.l2yy.webgis.service.UpdateLogService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author huangjiale
 * @since 2020-03-27
 */
@Service
public class UpdateLogServiceImpl extends ServiceImpl<UpdateLogMapper, UpdateLogDO> implements UpdateLogService {

    @Override
    public List<UpdateLogDTO> getLogList(Boolean reverse) {
        LambdaQueryWrapper<UpdateLogDO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UpdateLogDO::getDeleted, CommonConstant.NOT_DELETED);
        if (reverse){
            lambdaQueryWrapper.orderByAsc(UpdateLogDO::getCreateDt);
        }else {
            lambdaQueryWrapper.orderByDesc(UpdateLogDO::getCreateDt);
        }

        List<UpdateLogDTO> collect = this.list(lambdaQueryWrapper).stream().map(a -> {
                    UpdateLogDTO updateLogDTO = new UpdateLogDTO();
                    updateLogDTO.setContent(a.getUpdateMsg());
                    updateLogDTO.setTimestamp(a.getCreateDt());
                    updateLogDTO.setColor(randomHexStr(6));
                    return updateLogDTO;
                }).collect(Collectors.toList());
        return collect;
    }

    @Override
    public Boolean addLog(String msg) {
        UpdateLogDO updateLogDO = new UpdateLogDO();
        updateLogDO.setUpdateMsg(msg);
        boolean save = save(updateLogDO);
        return save;
    }

    public String randomHexStr(int len) {
        try {
            StringBuffer result = new StringBuffer();
            result.append("#");
            for (int i = 0; i < len; i++) {
                //随机生成0-15的数值并转换成16进制
                result.append(Integer.toHexString(new Random().nextInt(16)));
            }
            return result.toString().toUpperCase();
        } catch (Exception e) {
            System.out.println("获取16进制字符串异常，返回默认...");
            return "00CCCC";
        }
    }
}
