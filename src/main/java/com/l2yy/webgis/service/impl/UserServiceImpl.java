package com.l2yy.webgis.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.l2yy.webgis.constant.CommonConstant;
import com.l2yy.webgis.dto.LoginRequest;
import com.l2yy.webgis.entity.UserDO;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import com.l2yy.webgis.mapper.UserMapper;
import com.l2yy.webgis.service.UserService;
import com.l2yy.webgis.util.AESUtil;
import com.l2yy.webgis.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/28 10:14 下午
 * @description：
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, UserDO> implements UserService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map Login(LoginRequest loginRequest) {
        String aesEncode = AESUtil.aesEncode(loginRequest.getPassword());

        List<UserDO> list = lambdaQuery().eq(UserDO::getName, loginRequest.getUsername()).list();
        if (list == null || CollectionUtils.isEmpty(list)) {
            throw new BizException(CommonBizCodeEnum.NOT_EXIST_USERNAME);
        }
        List<UserDO> list1 = lambdaQuery().eq(UserDO::getName, loginRequest.getUsername()).eq(UserDO::getPassword, aesEncode).list();
        if (list1 == null || CollectionUtils.isEmpty(list1)) {
            throw new BizException(CommonBizCodeEnum.NOT_EXIST_PASSWORD);
        }
        String token = TokenUtil.getInstance().makeToken();

        stringRedisTemplate.opsForValue().set(token, loginRequest.getUsername(), Duration.ofSeconds(600));
        Map<String, String> userMap = new HashMap<>(1);
        userMap.put("token", token);
        return userMap;
    }

    @Override
    public Map getUserInfo(String token) {
        String userName = stringRedisTemplate.opsForValue().get(token);
        if (userName == null || StringUtils.isEmpty(userName)) {
            throw new BizException(CommonBizCodeEnum.EXPIRE_TOKEN);
        }
        if (userName != null && !StringUtils.isEmpty(userName)) {
            stringRedisTemplate.opsForValue().set(token, userName, Duration.ofSeconds(600));
        }
        UserDO one = lambdaQuery().eq(UserDO::getName, userName).one();
        String s = CommonConstant.AUTH.get(one.getAuth());
        Map<String, Object> map = new HashMap<>();
        map.put("roles", new String[]{s});
        map.put("introduction", one.getIntroduction());
//        https://wpimg.wallstcn.com/577965b9-bb9e-4e02-9f0c-095b41417191
//        https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif
        map.put("avatar", one.getAvatar());
        map.put("name", one.getName());
        return map;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean register(LoginRequest loginRequest) {
        List<UserDO> list = lambdaQuery().eq(UserDO::getName, loginRequest.getUsername()).list();
        boolean save = false;
        if (list == null || CollectionUtils.isEmpty(list)) {
            UserDO userDO = new UserDO();
            userDO.setName(loginRequest.getUsername());
            String aesEncode = AESUtil.aesEncode(loginRequest.getPassword());
            userDO.setPassword(aesEncode);
            save = this.save(userDO);
        }
        return save;
    }
}
