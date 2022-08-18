package com.personal.pojo.vo;

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
public class ShopcartVO {

    private String itemId;
    private String itemImgUrl;
    private String itemName;
    private String specId;
    private String specName;
    private String priceDiscount;
    private String priceNormal;
}
