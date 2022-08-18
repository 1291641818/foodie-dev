package com.personal.controller;

import com.personal.enums.OrderStatusEnum;
import com.personal.enums.PayMethod;
import com.personal.pojo.OrderStatus;
import com.personal.pojo.bo.ShopcartBO;
import com.personal.pojo.bo.SubmitOrderBO;
import com.personal.pojo.vo.MerchantOrdersVO;
import com.personal.pojo.vo.OrderVO;
import com.personal.service.OrderService;
import com.personal.utils.CookieUtils;
import com.personal.utils.JSONResult;
import com.personal.utils.JsonUtils;
import com.personal.utils.RedisOperator;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(value = "订单相关", tags = {"订单相关的api接口"})
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController {

    @Resource
    OrderService orderService;

    @Resource
    RestTemplate restTemplate;

    @Resource
    RedisOperator redisOperator;

    @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
    @PostMapping("/create")
    public JSONResult create(
            @RequestBody SubmitOrderBO submitOrderBO,
            HttpServletRequest request,
            HttpServletResponse response) {

        if (!submitOrderBO.getPayMethod().equals(PayMethod.ALIPAY.type)
                && !submitOrderBO.getPayMethod().equals(PayMethod.WEIXIN.type)) {
            return JSONResult.errorMsg("支付方式不支持");
        }

        String shopCartJson = redisOperator.get(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId());
        if (StringUtils.isBlank(shopCartJson)) {
            return JSONResult.errorMsg("购物车数据不正确");
        }
        List<ShopcartBO> shopCartList = JsonUtils.jsonToList(shopCartJson, ShopcartBO.class);

        // 1. 创建订单
        OrderVO orderVO = orderService.createOrder(shopCartList, submitOrderBO);
        String orderId = orderVO.getOrderId();

        // 2. 创建订单以后，移除购物车(redis)中已结算（已提交）的商品，并同步到前端的cookie
        shopCartList.removeAll(orderVO.getToBeRemovedShopCartList());
        redisOperator.set(FOODIE_SHOPCART + ":" + submitOrderBO.getUserId(), JsonUtils.objectToJson(shopCartList));
        CookieUtils.setCookie(request, response, FOODIE_SHOPCART, JsonUtils.objectToJson(shopCartList), true);

        // 3. TODO 向支付中心发送当前订单，用于保存支付中心的订单数据
        MerchantOrdersVO merchantOrdersVO = orderVO.getMerchantOrdersVO();
        merchantOrdersVO.setReturnUrl(PAY_RETURN_URL);

        // 为了方便测试购买，所以所有的支付金额都统一改为1分钱
        merchantOrdersVO.setAmount(1);

        // 3.1构建http请求
        HttpHeaders headers = new HttpHeaders();
        //MediaType里面有很多常量,如application/json,application/xml...
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("imoocUserId", "imooc");
        headers.add("password", "imooc");

        HttpEntity<MerchantOrdersVO> entity = new HttpEntity<>(merchantOrdersVO, headers);

        //对方以post方式接收
        ResponseEntity<JSONResult> responseEntity
                = restTemplate.postForEntity(PAYMENT_URL, entity, JSONResult.class);
        //返回值
        JSONResult paymentResult = responseEntity.getBody();
        if (paymentResult == null || paymentResult.getStatus() != 200) {
            return JSONResult.errorMsg("支付中心订单创建失败,请联系管理员");
        }
        return JSONResult.ok(orderId);
    }

    @ApiOperation(value = "通知订单改为支付代发货状态", notes = "通知订单改为支付代发货状态", httpMethod = "POST")
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(@RequestParam String merchantOrderId) {
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
    }

    @ApiOperation(value = "查询订单支付状态", notes = "查询订单支付状态", httpMethod = "POST")
    @PostMapping("/getPaidOrderInfo")
    public JSONResult getPaidOrderInfo(@RequestParam String orderId) {
        OrderStatus orderStatus = orderService.queryOrderStatusInfo(orderId);
        return JSONResult.ok(orderStatus);
    }


}
