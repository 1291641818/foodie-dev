package com.personal.enums;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description 商品评价等级枚举
 * @date 2022-06-22 23:59
 *
 */
public enum CommentLevel {

    GOOD(1, "好评"),
    NORMAL(2, "中评"),
    BAD(3, "差评"),;

    private final Integer code;
    private final String value;

    CommentLevel(Integer code, String value) {
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
