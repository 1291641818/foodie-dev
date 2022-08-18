package com.personal.controller;

import com.personal.pojo.Orders;
import com.personal.service.center.MyOrdersService;
import com.personal.utils.JSONResult;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author aXuan
 * @version V1.0
 * @title
 * @description
 * @date 2022-06-26 00:42
 */
public class BaseController {

    public static final int COMMON_PAGE_SIZE = 10;

    public static final int PAGE_SIZE = 20;

    public static final String FOODIE_SHOPCART = "shopcart";

    public static final String REDIS_USER_TOKEN = "redis_user_token";

    /**
     * 微信支付成功 -> 支付中心 -> 本地系统 -> 回调通知的url
     */
    public static final String PAY_RETURN_URL = "http://cnn.natapp1.cc/foodie-dev-api/orders/notifyMerchantOrderPaid";

    /**
     * 支付中心的调用地址
     */
    public static final String PAYMENT_URL = "http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

    /**
     * 图片上传路径 这个不需要了，因为已经在file-upload-dev.properties里配置了
     */
    /*public static final String IMG_USER_FACE_PATH = "G:" + File.separator
            + "code" + File.separator + "cache file" + File.separator + "img";*/


    @Autowired
    public MyOrdersService myOrdersService;

    /**
     * 用于验证用户和订单是否有关联关系，避免非法用户调用
     *
     * @return
     */
    public JSONResult checkUserOrder(String userId, String orderId) {
        Orders order = myOrdersService.queryMyOrder(userId, orderId);
        if (order == null) {
            return JSONResult.errorMsg("订单不存在！");
        }
        return JSONResult.ok(order);
    }
}
