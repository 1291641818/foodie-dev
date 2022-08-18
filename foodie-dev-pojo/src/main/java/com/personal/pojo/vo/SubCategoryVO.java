package com.personal.pojo.vo;

import lombok.Data;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 14:30
 *
 */

/**
 * 二级分类VO
 */
@Data
public class SubCategoryVO {
    private Integer subId;
    private String subName;
    private Integer subType;
    private Integer subFatherId;
}
