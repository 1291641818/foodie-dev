package com.personal.service.impl;

import com.personal.enums.OrderStatusEnum;
import com.personal.enums.YesOrNo;
import com.personal.mapper.OrderItemsMapper;
import com.personal.mapper.OrderStatusMapper;
import com.personal.mapper.OrdersMapper;
import com.personal.pojo.*;
import com.personal.pojo.bo.ShopcartBO;
import com.personal.pojo.bo.SubmitOrderBO;
import com.personal.pojo.vo.MerchantOrdersVO;
import com.personal.pojo.vo.OrderVO;
import com.personal.service.AddressService;
import com.personal.service.ItemService;
import com.personal.service.OrderService;
import com.personal.utils.DateUtil;
import org.apache.commons.lang3.time.DateUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Resource
    private AddressService addressService;

    @Resource
    private ItemService itemService;

    @Resource
    private OrderItemsMapper orderItemsMapper;

    @Resource
    private OrdersMapper ordersMapper;

    @Resource
    private OrderStatusMapper orderStatusMapper;

    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public OrderVO createOrder(List<ShopcartBO> shopCartList, SubmitOrderBO submitOrderBO) {

        String userId = submitOrderBO.getUserId();
        String addressId = submitOrderBO.getAddressId();
        String itemSpecIds = submitOrderBO.getItemSpecIds();
        Integer payMethod = submitOrderBO.getPayMethod();
        String leftMsg = submitOrderBO.getLeftMsg();
        // 包邮费用设置为0
        Integer postAmount = 0;

        String orderId = sid.nextShort();

        UserAddress address = addressService.queryUserAddres(userId, addressId);

        // 1. 新订单数据保存
        Orders newOrder = new Orders();
        newOrder.setId(orderId);
        newOrder.setUserId(userId);

        newOrder.setReceiverName(address.getReceiver());
        newOrder.setReceiverMobile(address.getMobile());
        newOrder.setReceiverAddress(address.getProvince() + " "
                + address.getCity() + " "
                + address.getDistrict() + " "
                + address.getDetail());

//        newOrder.setTotalAmount();
//        newOrder.setRealPayAmount();
        newOrder.setPostAmount(postAmount);

        newOrder.setPayMethod(payMethod);
        newOrder.setLeftMsg(leftMsg);

        newOrder.setIsComment(YesOrNo.NO.getCode());
        newOrder.setIsDelete(YesOrNo.NO.getCode());
        newOrder.setCreatedTime(new Date());
        newOrder.setUpdatedTime(new Date());


        // 2. 循环根据itemSpecIds保存订单商品信息表
        String itemSpecIdArr[] = itemSpecIds.split(",");
        Integer totalAmount = 0;    // 商品原价累计
        Integer realPayAmount = 0;  // 优惠后的实际支付价格累计
        List<ShopcartBO> toBeRemovedShopCartList = new ArrayList<>();
        for (String itemSpecId : itemSpecIdArr) {

            // 整合redis后，商品购买的数量重新从redis的购物车中获取
            ShopcartBO shopCartItem = getByCountsFromShopCart(shopCartList, itemSpecId);

            assert shopCartItem != null;
            int buyCounts = shopCartItem.getBuyCounts();
            toBeRemovedShopCartList.add(shopCartItem);

            // 2.1 根据规格id，查询规格的具体信息，主要获取价格
            ItemsSpec itemSpec = itemService.queryItemSpecById(itemSpecId);
            totalAmount += itemSpec.getPriceNormal() * buyCounts;
            realPayAmount += itemSpec.getPriceDiscount() * buyCounts;

            // 2.2 根据商品id，获得商品信息以及商品图片
            String itemId = itemSpec.getItemId();
            Items item = itemService.queryItemById(itemId);
            String imgUrl = itemService.queryItemMainImgById(itemId);

            // 2.3 循环保存子订单数据到数据库
            String subOrderId = sid.nextShort();
            OrderItems subOrderItem = new OrderItems();
            subOrderItem.setId(subOrderId);
            subOrderItem.setOrderId(orderId);
            subOrderItem.setItemId(itemId);
            subOrderItem.setItemName(item.getItemName());
            subOrderItem.setItemImg(imgUrl);
            subOrderItem.setBuyCounts(buyCounts);
            subOrderItem.setItemSpecId(itemSpecId);
            subOrderItem.setItemSpecName(itemSpec.getName());
            subOrderItem.setPrice(itemSpec.getPriceDiscount());
            orderItemsMapper.insert(subOrderItem);

            // 2.4 在用户提交订单以后，规格表中需要扣除库存
            itemService.decreaseItemSpecStock(itemSpecId, buyCounts);
        }

        newOrder.setTotalAmount(totalAmount);
        newOrder.setRealPayAmount(realPayAmount);
        ordersMapper.insert(newOrder);

        // 3. 保存订单状态表
        OrderStatus waitPayOrderStatus = new OrderStatus();
        waitPayOrderStatus.setOrderId(orderId);
        waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        waitPayOrderStatus.setCreatedTime(new Date());
        orderStatusMapper.insert(waitPayOrderStatus);

        // 4. 构建商户订单，用于传给支付中心
        MerchantOrdersVO merchantOrdersVO = new MerchantOrdersVO();
        merchantOrdersVO.setMerchantOrderId(orderId);
        merchantOrdersVO.setMerchantUserId(userId);
        merchantOrdersVO.setAmount(realPayAmount + postAmount);
        merchantOrdersVO.setPayMethod(payMethod);

        // 5. 构建自定义订单vo
        OrderVO orderVO = new OrderVO();
        orderVO.setOrderId(orderId);
        orderVO.setMerchantOrdersVO(merchantOrdersVO);
        orderVO.setToBeRemovedShopCartList(toBeRemovedShopCartList);
        return orderVO;
    }

    /**
     * 从redis的购物车中获取商品，目的是得到counts信息
     */
    private ShopcartBO getByCountsFromShopCart(List<ShopcartBO> shopCartList, String itemSpecId) {
        for (ShopcartBO shopCart : shopCartList
        ) {
            if (shopCart.getSpecId().equals(itemSpecId)) {
                return shopCart;
            }
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateOrderStatus(String merchantOrderId, Integer orderStatus) {
        OrderStatus paidStatus = new OrderStatus();
        paidStatus.setOrderId(merchantOrderId);
        paidStatus.setOrderStatus(orderStatus);
        paidStatus.setPayTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(paidStatus);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public OrderStatus queryOrderStatusInfo(String orderId) {
        return orderStatusMapper.selectByPrimaryKey(orderId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Error.class})
    public void closeOrder() {
        //1. 查询所有未付款订单，判断时间是否超时（1天），超时则关闭
        OrderStatus queryOrder = new OrderStatus();
        queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
        List<OrderStatus> orderStatusList = orderStatusMapper.select(queryOrder);
        orderStatusList.stream().forEach(item -> {
            Date createTime = item.getCreatedTime();
            int days = DateUtil.daysBetween(createTime, new Date());
            if (days >= 1) {
                //时间间隔超过一天，关闭订单
                doCloseOrder(item.getOrderId());
            }
        });
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {RuntimeException.class, Error.class})
    void doCloseOrder(String orderId) {
        OrderStatus closeOrderStatus = new OrderStatus();
        closeOrderStatus.setOrderId(orderId);
        closeOrderStatus.setOrderStatus(OrderStatusEnum.CLOSE.type);
        closeOrderStatus.setCloseTime(new Date());
        orderStatusMapper.updateByPrimaryKeySelective(closeOrderStatus);
    }

}
