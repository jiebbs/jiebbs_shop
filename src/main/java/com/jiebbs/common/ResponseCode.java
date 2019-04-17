package com.jiebbs.common;

/**
 * 响应状态码及状态枚举类
 * @author weijie
 * @version 1.0 2019-04-17
 */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),

    NEED_LOGIN(10,"NEED_LOGIN"),

    ERROR(1,"ERROR"),

    ILLEGAL_ARGUMENT(2,"ILLEGAL_ARGUMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
