package com.personal.service;

import com.personal.pojo.OrderStatus;
import com.personal.pojo.bo.ShopcartBO;
import com.personal.pojo.bo.SubmitOrderBO;
import com.personal.pojo.vo.OrderVO;

import java.util.List;

public interface OrderService {

    /**
     * 用于创建订单相关信息
     *
     * @param submitOrderBO
     */
    public OrderVO createOrder(List<ShopcartBO> shopCartList, SubmitOrderBO submitOrderBO);


    /**
     * 更新订单状态
     *
     * @param merchantOrderId
     * @param orderStatus
     */
    void updateOrderStatus(String merchantOrderId, Integer orderStatus);

    /**
     * 查询订单状态
     *
     * @param orderId
     * @return
     */
    OrderStatus queryOrderStatusInfo(String orderId);

    /**
     * 关闭超时订单
     */
    void closeOrder();
}
