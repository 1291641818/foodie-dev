package com.personal.interceptor;

import com.personal.utils.JSONResult;
import com.personal.utils.JsonUtils;
import com.personal.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-08-19 00:57
 */
public class UserTokenInterceptor implements HandlerInterceptor {

    private static final String REDIS_USER_TOKEN = "redis_user_token";


    private static final Logger logger = LoggerFactory.getLogger(UserTokenInterceptor.class);

    @Resource
    RedisOperator redisOperator;

    /**
     * before access interface
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");

        // if userId is blank or token is blank, intercept this request
        if (StringUtils.isBlank(userId) || StringUtils.isBlank(userToken)) {
            logger.error("user attempted to access without userId or token ");
            returnErrorResponse(response, JSONResult.errorMsg("进行操作前请先登录"));
            return false;
        }

        // tokenInRedis must exist
        String tokenInRedis = redisOperator.get(REDIS_USER_TOKEN + ":" + userId);
        if (StringUtils.isBlank(tokenInRedis)) {
            logger.error("user attempted to access without log in");
            returnErrorResponse(response, JSONResult.errorMsg("进行操作前请先登录"));
            return false;
        }

        // userToken and tokenInRedis must be consistent
        if (!userToken.equals(tokenInRedis)) {
            logger.error("user attempted to log in remotely ");
            returnErrorResponse(response, JSONResult.errorMsg("进行操作前请先登录"));
            return false;
        }

        return true;
    }

    /**
     * flush error msg to user
     */
    private void returnErrorResponse(HttpServletResponse response, JSONResult jsonResult) {
        try (ServletOutputStream out = response.getOutputStream()) {
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            out.write(JsonUtils.objectToJson(jsonResult).getBytes());
            out.flush();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
