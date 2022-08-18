package com.personal.pojo.vo;

import lombok.Data;

import java.util.List;

/**
 * 一级分类VO
 */
@Data
public class CategoryVO {
    private Integer id;
    private String name;
    private Integer type;
    private Integer fatherId;

    private List<SubCategoryVO> subCatList;

}