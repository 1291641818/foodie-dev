package com.personal.controller;

import com.personal.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-07-26 21:50
 */
@ApiIgnore
@RestController
@RequestMapping("/redis")
public class RedisController {


    @Autowired
    private RedisOperator redisOperator;

    @GetMapping("/set")
    public Object set(String key, String value) {
        redisOperator.set(key, value);



        //redisOperator.set(key, value);
        return "OK";
    }

    @GetMapping("/get")
    public String get(String key) {
//        return (String)redisTemplate.opsForValue().get(key);
        return redisOperator.get(key);
    }

    @GetMapping("/delete")
    public Object delete(String key) {
//        redisTemplate.delete(key);
        redisOperator.del(key);
        return "OK";
    }

    /**
     * 大量key查询
     * @param keys
     * @return
     */
    @GetMapping("/getALot")
    public Object getALot(String... keys) {
        List<String> resutl = new ArrayList<>();
        for (String k:keys) {
            resutl.add(redisOperator.get(k));
        }
        return resutl;
    }

    /**
     * 批量查询 mget
     * @param keys
     * @return
     */
    @GetMapping("/mget")
    public Object mget(String... keys) {
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.mget(keysList);
    }

    /**
     * 批量查询 pipeline
     * @param keys
     * @return
     */
    @GetMapping("/batchGet")
    public Object batchGet(String... keys) {
        List<String> keysList = Arrays.asList(keys);
        return redisOperator.batchGet(keysList);
    }

}


