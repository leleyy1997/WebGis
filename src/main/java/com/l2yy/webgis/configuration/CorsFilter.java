package com.l2yy.webgis.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@WebFilter(filterName = "SimpleCORSFilter",urlPatterns ="/*" )
public class CorsFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(CorsFilter.class);
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        logger.info("请求拦截："+ ((HttpServletRequest) request).getRequestURI());
        /*
         *  设置允许跨域请求
         */
        httpServletResponse.setHeader("Access-Control-Allow-Headers", "content-type, accept");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST");
        httpServletResponse.setStatus(200);
        httpServletResponse.setContentType("text/plain;charset=utf-8");
        httpServletResponse.setCharacterEncoding("utf-8");
        httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        httpServletResponse.setHeader("Access-Control-Max-Age", "0");
        httpServletResponse.setHeader("Access-Control-Allow-Headers",
                "Origin, No-Cache, X-Requested-With, If-Modified-Since, Pragma, Last-Modified, Cache-Control, Expires, Content-Type, X-E4M-With,userId,token,WG-Token, Authorization");
        httpServletResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpServletResponse.setHeader("XDomainRequestAllowed", "1");

         /*
            过虑 OPTIONS 请求
         */
        String type = httpServletRequest.getMethod();
        /**
         * 拿到请求的ip
         */
        logger.info(request.getRemoteHost());
        if (type.toUpperCase().equals("OPTIONS")) {
            return;
        }

//        String header = httpServletRequest.getHeader("X-Token");
//        if (header != null && !StringUtils.isEmpty(header)){
//            String userName = stringRedisTemplate.opsForValue().get(header);
//            if (userName == null || StringUtils.isEmpty(userName)) {
//                throw new BizException(CommonBizCodeEnum.EXPIRE_TOKEN);
//            }
//            if (userName != null && !StringUtils.isEmpty(userName)) {
//                stringRedisTemplate.opsForValue().set(header, userName, Duration.ofSeconds(600));
//            }
//        }
//        logger.info(header);

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
//        String isCrossStr = filterConfig.getInitParameter("IsCross");
//        isCross = isCrossStr.equals("true") ? true : false;
//        System.out.println(isCrossStr);
    }
    @Override
    public void destroy() {
//        isCross = false;
    }

}