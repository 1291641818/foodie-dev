package com.personal.controller.center;

import com.personal.pojo.Users;
import com.personal.service.center.CenterUserService;
import com.personal.utils.JSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-30 21:00
 */
@Api(value = "用户中心", tags = {"用户中心相关接口相关的api"})
@RestController
@RequestMapping("center")
public class CenterController {

    @Resource
    private CenterUserService centerUserService;

    @ApiOperation(value = "获取用户信息", notes = "获取用户信息", httpMethod = "GET")
    @GetMapping("userInfo")
    public JSONResult userInfo(
            @ApiParam(name = "userId", value = "用户id", required = true)
            @RequestParam String userId) {

        Users user = centerUserService.queryUserInfo(userId);
        return JSONResult.ok(user);
    }
}
