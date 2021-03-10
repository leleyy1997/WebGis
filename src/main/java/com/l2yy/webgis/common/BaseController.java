package com.l2yy.webgis.common;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public abstract class BaseController {

    protected ThreadLocal<HttpServletRequest> request = new ThreadLocal<>();

    protected ThreadLocal<HttpServletResponse> response = new ThreadLocal<>();

    @Autowired
    protected ConfigurableApplicationContext applicationContext;


    /**
     * 获取 HttpServletRequest
     *
     * @return
     */
    protected HttpServletRequest getHttpServletRequest() {
        return request.get();
    }

    /**
     * 获取 HttpServletResponse
     *
     * @return
     */
    protected HttpServletResponse getHttpServletResponse() {
        return response.get();
    }

    @ModelAttribute
    public void initReqAndRes(HttpServletRequest request, HttpServletResponse response) {
        this.request.set(request);
        this.response.set(response);
    }


}
