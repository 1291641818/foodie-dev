package com.personal.pojo.vo;

import lombok.Data;

import java.util.Date;

/**
 * 用于展示商品搜索列表的VO
 */
@Data
public class SearchItemsVO {

    private String itemId;
    private String itemName;
    private Integer sellCounts;
    private String imgUrl;
    private Integer price;

}
