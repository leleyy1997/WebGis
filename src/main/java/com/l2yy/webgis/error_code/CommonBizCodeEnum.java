package com.l2yy.webgis.error_code;

/**
 * 错误码范围 0~1000
 */
public enum CommonBizCodeEnum implements BizCodeEnum {

    FAIL(0, "失败"),

    SUCCESS(1, "成功"),

    PARAM_IS_NULL(2, "参数为空"),

    PARAM_NOT_RIGHT(3, "参数错误"),

    DATA_IS_NULL(4, "该记录不存在"),

    DATA_NOT_PERMISSION(5, "该记录无权限操作"),

    NOT_EXIST_USERNAME(6,"用户名不存在"),

    NOT_EXIST_PASSWORD(7,"用户名或密码错误"),

    EXPIRE_TOKEN(8,"会话过期，请重新登录"),

    NOT_SUPPORT_TYPE(9,"不支持安居客数据的热力图"),

    NOT_SELECT_CITY(10,"请先选择城市"),

    TOKEN_EXPIRE(50014,"会话过期，请重新登录"),

    GET_DATA_IS_EMPTY(4010, "获取数据为空"),

    FILE_IS_EMPTY(4013, "上传文件为空"),

    EXCEL_EXPORT_ERROR(4228, "Excel 导出异常"),

    EXCEL_EXPORT_NO_DATA(4229,"当前城市暂无数据"),


    SYSTEM_EXCEPTION(5001, "系统异常"),

    ;

    public final int code;

    public final String message;

    CommonBizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
