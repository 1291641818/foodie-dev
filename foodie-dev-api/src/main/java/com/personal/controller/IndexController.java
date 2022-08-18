package com.personal.controller;

import com.personal.enums.YesOrNo;
import com.personal.pojo.Carousel;
import com.personal.pojo.Category;
import com.personal.pojo.vo.CategoryVO;
import com.personal.pojo.vo.NewItemsVO;
import com.personal.service.CarouselService;
import com.personal.service.CategoryService;
import com.personal.utils.JSONResult;
import com.personal.utils.JsonUtils;
import com.personal.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 10:24
 */
@Api(value = "首页", tags = {"首页展示的相关接口"})
@RestController
@RequestMapping("/index")
public class IndexController {

    @Resource
    CarouselService carouselService;

    @Resource
    CategoryService categoryService;

    @Resource
    RedisOperator redisOperator;

    @ApiOperation(value = "获取首页轮播图列表", notes = "获取首页轮播图列表", httpMethod = "GET")
    @GetMapping("/carousel")
    public JSONResult queryCarousel() {
        List<Carousel> list;

        //若redis缓存中有数据，就直接取缓存，否则要查询数据库
        String carouselStr = redisOperator.get("carousel");
        if (StringUtils.isBlank(carouselStr)) {
            List<Carousel> carousels = carouselService.queryAll(YesOrNo.YES.getCode());
            redisOperator.set("carousel", JsonUtils.objectToJson(carousels));
            list = carousels;
        } else {
            list = JsonUtils.jsonToList(carouselStr, Carousel.class);
        }

        return JSONResult.ok(list);
    }

    /**
     * 1. 后台运营系统，一旦广告（轮播图）发生更改，就可以删除缓存，然后重置
     * 2. 定时重置，比如每天凌晨三点重置
     * 3. 每个轮播图都有可能是一个广告，每个广告都会有一个过期时间，过期了，再重置
     */

    /**
     * 首页分类展示需求:
     * 1. 第一次刷新主页查询大分类，渲染展示到首页
     * 2. 如果鼠标移动到大分类上，则加载其子分类的内容，如果存在子分类，则不需要加载（懒加载）
     */
    @ApiOperation(value = "获取商品分类(一级分类)", notes = "获取商品分类(一级分类)", httpMethod = "GET")
    @GetMapping("/cats")
    public JSONResult cats() {
        List<Category> list;

        //若redis缓存中有数据，就直接取缓存，否则要查询数据库
        String categoryStr = redisOperator.get("category");
        if (StringUtils.isBlank(categoryStr)) {
            list = categoryService.queryAllRootLevelCat();
            redisOperator.set("category", JsonUtils.objectToJson(list));
        } else {
            list = JsonUtils.jsonToList(categoryStr, Category.class);
        }

        return JSONResult.ok(list);
    }

    @ApiOperation(value = "获取商品子分类", notes = "获取商品子分类", httpMethod = "GET")
    @GetMapping("/subCat/{rootCatId}")
    public JSONResult subCat(@PathVariable @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
                                     Integer rootCatId) {
        if (rootCatId == null) {
            return JSONResult.errorMsg("分类不存在");
        }

        List<CategoryVO> list;

        String catsStr = redisOperator.get("subCat:" + rootCatId);
        if (StringUtils.isBlank(catsStr)) {
            list = categoryService.getSubCatList(rootCatId);
            if (CollectionUtils.isEmpty(list)) {
                //若查询的列表为空的话，也需要向redis中添加一个空的expires=300秒的值，防止缓存穿透
                redisOperator.set("subCat:"+rootCatId,null,300);
            } else {
                redisOperator.set("subCat:" + rootCatId, JsonUtils.objectToJson(list));
            }
        } else {
            list = JsonUtils.jsonToList(catsStr, CategoryVO.class);
        }

        return JSONResult.ok(list);
    }

    @ApiOperation(value = "查询每个一级分类下的最新6条商品数据", notes = "查询每个一级分类下的最新6条商品数据", httpMethod = "GET")
    @GetMapping("/sixNewItems/{rootCatId}")
    public JSONResult sixNewItems(
            @ApiParam(name = "rootCatId", value = "一级分类id", required = true)
            @PathVariable Integer rootCatId) {

        if (rootCatId == null) {
            return JSONResult.errorMsg("分类不存在");
        }

        List<NewItemsVO> list = categoryService.getSixNewItemsLazy(rootCatId);
        return JSONResult.ok(list);
    }

}
