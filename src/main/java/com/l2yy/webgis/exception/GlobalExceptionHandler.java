package com.l2yy.webgis.exception;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.l2yy.webgis.error_code.ErrorDetail;
import com.l2yy.webgis.util.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.Optional;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    @ExceptionHandler({Exception.class})
    public CommonResponse handleException(Exception e, HttpServletRequest request) {
        log.error(String.format("request url [%s] exception", request.getServletPath()), e);
        return CommonResponse.buildException(String.format("unknown exception: [%s]", e.getMessage()));
    }

    @ResponseBody
    @ExceptionHandler({BizException.class})
    public CommonResponse handleBizException(BizException e, HttpServletRequest request) {
        log.error(String.format("request url [%s] biz exception, detail: [%s]", request.getServletPath(), JSONObject.toJSONString(e.getBizCode())));
        return CommonResponse.buildFail(e.getBizCode());
    }

    @ResponseBody
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public CommonResponse handleBindException(MethodArgumentNotValidException exception) {
        String firstMessage = "";
        List<ObjectError> allErrors = exception.getBindingResult().getAllErrors();
        if (!CollectionUtils.isEmpty(allErrors)) {
            firstMessage = allErrors.get(0).getDefaultMessage();
        }
        return CommonResponse.buildFail(new ErrorDetail(firstMessage));
    }

    @ResponseBody
    @ExceptionHandler({ConstraintViolationException.class})
    public CommonResponse handleConstraintViolationException(
            ConstraintViolationException exception) {
        String firstMessage = "";
        Optional<ConstraintViolation<?>> firstResult = exception.getConstraintViolations().stream()
                .findFirst();
        if (firstResult.isPresent()) {
            firstMessage = firstResult.get().getMessage();
        }
        return CommonResponse.buildFail(new ErrorDetail(firstMessage));
    }
}
