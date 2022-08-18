package com.personal.pojo.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description 用于展示商品评价数量的VO
 * @date 2022-06-25 22:37
 *
 */
@Data
@Builder
public class CommentLevelCountsVO {

    private Integer totalCounts;
    private Integer goodCounts;
    private Integer normalCounts;
    private Integer badCounts;

}
