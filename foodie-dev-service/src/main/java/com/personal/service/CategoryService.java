package com.personal.service;

import com.personal.pojo.Carousel;
import com.personal.pojo.Category;
import com.personal.pojo.vo.CategoryVO;
import com.personal.pojo.vo.NewItemsVO;

import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 10:19
 *
 */
public interface CategoryService {

    List<Category> queryAllRootLevelCat();

    List<CategoryVO> getSubCatList(Integer rootCatId);

    List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId);
}
