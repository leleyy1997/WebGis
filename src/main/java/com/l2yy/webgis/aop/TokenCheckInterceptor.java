package com.l2yy.webgis.aop;

import com.alibaba.fastjson.JSON;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import com.l2yy.webgis.exception.BizException;
import com.l2yy.webgis.util.CommonResponse;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;

/**
 * @author ：hjl
 * @date ：Created in 2020/4/11 2:17 下午
 * @description：
 */
@Aspect
@Component
public class TokenCheckInterceptor {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger = LoggerFactory.getLogger(TokenCheckInterceptor.class);

    public TokenCheckInterceptor() {
        System.out.println("===>check start");
    }

    @Pointcut("@annotation(com.l2yy.webgis.annotation.TokenCheck)")
    private void anyMethod() {
    }


    @Around("anyMethod()")
    public Object checkRequestHead(ProceedingJoinPoint joinPoint) throws Throwable {
        logger.info("before");
        logger.debug("===>check access token start:{}", joinPoint.getArgs());
        HttpServletRequest request =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        request.setCharacterEncoding("UTF-8");
        HttpServletResponse response =
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
        String token = request.getHeader("X-Token");
        logger.debug("===> request token is {}", token);
//        String ipAddr = request.getHeader("X-Real-IP");
//        if (ipAddr == null || ipAddr.equals("")) {
//            ipAddr = request.getRemoteAddr();
//        }
//        logger.info("request X-Real-IP ipAddr={}", ipAddr);

        if (token != null && !StringUtils.isEmpty(token)){
            String userName = stringRedisTemplate.opsForValue().get(token);
            if (userName == null || StringUtils.isEmpty(userName)) {
                throw new BizException(CommonBizCodeEnum.TOKEN_EXPIRE);
            }
            if (!StringUtils.isEmpty(userName)) {
                stringRedisTemplate.opsForValue().set(token, userName, Duration.ofSeconds(600));
                Object o = joinPoint.proceed();
                return o;
            }
        }

        return null;

//        if (helper.verifyToken(token, requestUri, requestMethod, queryParam, requestBody)) {
//            Object o = joinPoint.proceed();
//            long end = System.nanoTime();
//            logger.info("API deal time log {}:{}",
//                    joinPoint.getTarget().getClass() + "." + joinPoint.getSignature().getName(),
//                    (end - begin) / 1000000);
//            return o;
//        } else {
//            writeResponse(response,
//                     CommonResponse.buildFail(CommonBizCodeEnum.EXPIRE_TOKEN));
//            return null;
//        }
    }

    private void writeResponse(HttpServletResponse response, CommonResponse apiResponse) throws IOException {
        logger.info("===>token check failed.return response={}", JSON.toJSONString(apiResponse));
        String responseMap = JSON.toJSONString(apiResponse);
        response.setContentType("application/json;charset=utf-8");
        response.getOutputStream().write(responseMap.getBytes());
        response.flushBuffer();
    }


    @After("anyMethod()")
    public void after(){
        logger.info("after");
    }


}
