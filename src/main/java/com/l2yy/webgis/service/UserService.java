package com.l2yy.webgis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.l2yy.webgis.dto.LoginRequest;
import com.l2yy.webgis.entity.UserDO;

import java.util.Map;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/28 10:13 下午
 * @description：
 */
public interface UserService extends IService<UserDO> {

    Map Login(LoginRequest loginRequest);

    Map getUserInfo(String token);

    boolean register(LoginRequest loginRequest);
}
