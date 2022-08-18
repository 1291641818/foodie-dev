package com.personal.controller;

import com.personal.pojo.bo.ShopcartBO;
import com.personal.utils.JSONResult;
import com.personal.utils.JsonUtils;
import com.personal.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-26 15:07
 */
@Api(value = "购物车接口controller", tags = {"购物车接口相关的api"})
@RequestMapping("shopcart")
@RestController
public class ShopcartController extends BaseController {

    @Resource
    RedisOperator redisOperator;

    @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
    @PostMapping("/add")
    public JSONResult add(@RequestParam String userId,
                          @RequestBody ShopcartBO shopcartBO,
                          HttpServletRequest request,
                          HttpServletResponse response) {
        if (StringUtils.isBlank(userId)) {
            return JSONResult.errorMsg("");
        }

        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);

        List<ShopcartBO> shopCartList;

        //若redis缓存中没有用户购物车数据，则new一个，否则在原来数据的基础上添加数据
        if (StringUtils.isBlank(shopCartJson)) {
            shopCartList = new ArrayList<>();
            shopCartList.add(shopcartBO);
        } else {
            shopCartList = JsonUtils.jsonToList(shopCartJson, ShopcartBO.class);

            assert shopCartList != null;
            // 判断购物车中是否有该商品，如果有则增加数量；如果没有则新增商品;
            boolean isHaving = false;
            for (ShopcartBO sc : shopCartList
            ) {
                String tempSpecId = sc.getSpecId();
                if (tempSpecId.equals(shopcartBO.getSpecId())) {
                    sc.setBuyCounts(sc.getBuyCounts() + shopcartBO.getBuyCounts());
                    isHaving = true;
                    break;
                }
            }
            if (!isHaving) {
                shopCartList.add(shopcartBO);
            }
        }

        // 覆盖现有购物车中的数据
        redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartList));
        return JSONResult.ok();
    }

    @ApiOperation(value = "从购物车中删除商品", notes = "从购物车中删除商品", httpMethod = "POST")
    @PostMapping("/del")
    public JSONResult del(
            @RequestParam String userId,
            @RequestParam String itemSpecId,
            HttpServletRequest request,
            HttpServletResponse response
    ) {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
            return JSONResult.errorMsg("参数不能为空");
        }

        // 用户在页面删除购物车中的商品数据，如果此时用户已经登录，则需要同步删除redis购物车中的商品
        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + userId);
        if (StringUtils.isNotBlank(shopCartJson)) {
            // redis中已经有购物车了
            List<ShopcartBO> shopCartList = JsonUtils.jsonToList(shopCartJson, ShopcartBO.class);
            // 判断购物车中是否存在已有商品，如果有的话则删除
            for (ShopcartBO sc : shopCartList) {
                String tmpSpecId = sc.getSpecId();
                if (tmpSpecId.equals(itemSpecId)) {
                    shopCartList.remove(sc);
                    break;
                }
            }
            // 覆盖现有redis中的购物车
            redisOperator.set(FOODIE_SHOPCART + ":" + userId, JsonUtils.objectToJson(shopCartList));
        }

        return JSONResult.ok();
    }
}
