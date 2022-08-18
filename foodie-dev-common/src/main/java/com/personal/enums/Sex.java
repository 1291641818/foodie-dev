package com.personal.enums;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-22 23:59
 *
 */
public enum Sex {

    WOMEN(0, "女"),
    MAN(1, "男"),
    SECRET(2, "保密");

    private final Integer code;
    private final String value;

    Sex(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }
}
