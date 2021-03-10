package com.l2yy.webgis.util;

import com.l2yy.webgis.error_code.BizCodeEnum;
import com.l2yy.webgis.error_code.CommonBizCodeEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CommonResponse<T> {

    public static final int SYS_CODE_EXCEPTION = -1;

    public static final int SYS_CODE_FAIL = 0;

    public static final int SYS_CODE_SUCCESS = 200;

    /**
     * 是否请求成功：0-失败，1-成功，-1：异常
     */
    private Integer code;

    private Integer bizCode;

    private String msg;

    private T data;

    public CommonResponse(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public CommonResponse(Integer code, Integer bizCode, String msg) {
        this.code = code;
        this.bizCode = bizCode;
        this.msg = msg;
    }

    public CommonResponse(Integer code, Integer bizCode, String msg, T data) {
        this.code = code;
        this.bizCode = bizCode;
        this.msg = msg;
        this.data = data;
    }

    public static final CommonResponse buildException(String msg) {
        return new CommonResponse(SYS_CODE_EXCEPTION, msg);
    }

    public static final CommonResponse buildFail(int bizCode, String msg) {
        return new CommonResponse(SYS_CODE_FAIL, bizCode, msg);
    }

    public static final CommonResponse buildFail(BizCodeEnum bizCode) {
        return new CommonResponse(SYS_CODE_FAIL, bizCode.getCode(), bizCode.getMessage());
    }

    public static final <T> CommonResponse<T> buildFail(int bizCode, String msg, T data) {
        return new CommonResponse<>(SYS_CODE_FAIL, bizCode, msg, data);
    }

    public static final CommonResponse buildSuccess(int bizCode, String msg) {
        return new CommonResponse(SYS_CODE_SUCCESS, bizCode, msg);
    }

    public static final <T> CommonResponse<T> buildSuccess(int bizCode, String msg, T data) {
        return new CommonResponse<>(SYS_CODE_SUCCESS, bizCode, msg, data);
    }

    public static final CommonResponse buildSuccess() {
        return buildSuccess(CommonBizCodeEnum.SUCCESS.code, CommonBizCodeEnum.SUCCESS.message);
    }

    public static final <T> CommonResponse<T> buildSuccess(T data) {
        return buildSuccess(CommonBizCodeEnum.SUCCESS.code, CommonBizCodeEnum.SUCCESS.message, data);
    }
}
