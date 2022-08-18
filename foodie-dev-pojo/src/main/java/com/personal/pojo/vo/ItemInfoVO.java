package com.personal.pojo.vo;

import com.personal.pojo.Items;
import com.personal.pojo.ItemsImg;
import com.personal.pojo.ItemsParam;
import com.personal.pojo.ItemsSpec;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 商品详情VO
 */
@Builder
@Data
public class ItemInfoVO {

    private Items item;
    private List<ItemsImg> itemImgList;
    private List<ItemsSpec> itemSpecList;
    private ItemsParam itemParams;
}
