package com.l2yy.webgis.exception;


import com.l2yy.webgis.error_code.BizCodeEnum;
import lombok.Data;

@Data
public class BizException extends RuntimeException {

    private BizCodeEnum bizCode;

    public BizException(BizCodeEnum bizCode) {
        super(bizCode.getMessage());
        this.bizCode = bizCode;
    }
}
