package com.yuan.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.mall.order.controller.OrderSubmitRequestVo;
import com.yuan.mall.order.entity.OrderEntity;
import com.yuan.mall.order.vo.OrderConfirmVo;
import com.yuan.mall.order.vo.OrderDetailVo;
import com.yuan.mall.order.vo.OrderRequestVo;

import java.util.concurrent.ExecutionException;

/**
 * @author Yuan Diao
 * @date 2022/8/14
 */
public interface OrderService extends IService<OrderEntity> {

    OrderConfirmVo orderConfirm(Integer userId, OrderRequestVo request) throws ExecutionException, InterruptedException;

    String submitOrder(Integer userId, OrderSubmitRequestVo request);

    OrderEntity getOrderByOrderSn(String orderSn);

    void closeOrder(OrderEntity orderEntity);

    OrderDetailVo getOrderDetail(Integer userId, String orderSn);
}
