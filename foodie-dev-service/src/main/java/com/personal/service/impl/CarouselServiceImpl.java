package com.personal.service.impl;

import com.personal.mapper.CarouselMapper;
import com.personal.pojo.Carousel;
import com.personal.service.CarouselService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-24 10:20
 *
 */
@Service
public class CarouselServiceImpl implements CarouselService {
    @Resource
    CarouselMapper carouselMapper;

    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<Carousel> queryAll(Integer isShow) {
        Example example = new Example(Carousel.class);
        example.createCriteria().andEqualTo("isShow", isShow);
        example.orderBy("sort").desc();
        List<Carousel> carousels = carouselMapper.selectByExample(example);
        return carousels;
    }
}
