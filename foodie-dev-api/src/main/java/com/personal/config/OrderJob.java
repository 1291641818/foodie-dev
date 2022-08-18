package com.personal.config;

import com.personal.service.OrderService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-30 09:48
 */
@Component
public class OrderJob {

    @Resource
    private OrderService orderService;

    @Scheduled(cron = "0 0 0/1 * * ?")
    public void autoCloseOrder() {
        orderService.closeOrder();
    }
}
