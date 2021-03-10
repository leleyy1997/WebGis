package com.l2yy.webgis.error_code;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class ErrorDetail implements BizCodeEnum {

    private String message;

    @Override
    public int getCode() {
        return CommonBizCodeEnum.PARAM_NOT_RIGHT.code;
    }

    @Override
    public String getMessage() {
        return this.message;
    }
}
