package com.personal.enums;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description 是否枚举
 * @date 2022-06-22 23:59
 *
 */
public enum YesOrNo {

    NO(0, "否"),
    YES(1, "是");

    private final Integer code;
    private final String value;

    YesOrNo(Integer code, String value) {
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
