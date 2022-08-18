package com.personal.service;

import com.personal.pojo.Carousel;

import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 10:19
 *
 */
public interface CarouselService {

    public List<Carousel> queryAll(Integer isShow);
}
