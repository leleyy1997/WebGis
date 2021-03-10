package com.l2yy.webgis.controller;

import com.l2yy.webgis.dto.LoginRequest;
import com.l2yy.webgis.service.UserService;
import com.l2yy.webgis.util.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author ：hjl
 * @date ：Created in 2020/3/4 10:32 下午
 * @description：登录 Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @PostMapping("/login")
    public CommonResponse<Map> login(@RequestBody LoginRequest loginRequest) {
        return CommonResponse.buildSuccess(userService.Login(loginRequest));
    }

    @GetMapping("/info")
    public CommonResponse<Map> userInfo(@RequestParam("token") String token) {
        return CommonResponse.buildSuccess(userService.getUserInfo(token));
    }

    @PostMapping("/register")
    public CommonResponse<Boolean> register(@RequestBody LoginRequest loginRequest){
        return CommonResponse.buildSuccess(userService.register(loginRequest));
    }

    @PostMapping("/logout")
    public CommonResponse logout(){
        return CommonResponse.buildSuccess();
    }
}
