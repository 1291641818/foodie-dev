package com.test;

import com.personal.Application;
import com.personal.controller.BaseController;
import com.personal.controller.OrdersController;
import com.personal.utils.JSONResult;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tk.mybatis.spring.annotation.MapperScan;

import javax.annotation.Resource;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-21 16:49
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class TransTest {
    @Resource
    OrdersController ordersController;

    @Test
    public void testTrans(){
        JSONResult paidOrderInfo = ordersController.getPaidOrderInfo("220629C6T8KZSW94");
        System.out.println(paidOrderInfo);
    }
}
