package com.personal.pojo.bo;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-22 23:42
 *
 */
@Data
@ApiModel(value = "购物车对象BO", description = "从客户端由用户传入的数据封装在此entity中")
public class ShopcartBO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private Integer buyCounts;
    private String priceDiscount;
    private String priceNormal;
}
