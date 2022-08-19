package com.yuan.mall.order.controller;

import com.yuan.common.utils.R;
import com.yuan.mall.order.annotation.LoginUser;
import com.yuan.mall.order.entity.OrderEntity;
import com.yuan.mall.order.service.OrderService;
import com.yuan.mall.order.vo.OrderConfirmVo;
import com.yuan.mall.order.vo.OrderDetailVo;
import com.yuan.mall.order.vo.OrderRequestVo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Yuan Diao
 * @date 2022/8/14
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Resource
    private OrderService orderService;

    @PostMapping("/confirm")
    public R orderConfirm(@LoginUser Integer userId, @RequestBody OrderRequestVo request) throws ExecutionException, InterruptedException {
        if (userId != null){
            OrderConfirmVo r = orderService.orderConfirm(userId, request);
            return R.ok().put("data", r);
        }
        return R.unLogin();
    }

    @PostMapping("/submit")
    public R submitOrder(@LoginUser Integer userId, @RequestBody OrderSubmitRequestVo params){

        if (userId != null){
            String r = orderService.submitOrder(userId, params);
            if (r != null){
                return R.ok().put("data", r);
            } else {
                return R.error();
            }
        }
        return R.unLogin();
    }


    /**
     * 根据订单编号查询订单状态
     * @param orderSn
     * @return
     */
    @GetMapping(value = "/status/{orderSn}")
    public R getOrderStatus(@PathVariable("orderSn") String orderSn) {
        OrderEntity orderEntity = orderService.getOrderByOrderSn(orderSn);
        return R.ok().setData(orderEntity);
    }

    @GetMapping("/detail")
    public R getOrderDetail(@LoginUser Integer userId, @RequestParam("order_sn") String orderSn){
        System.out.println(userId);
        System.out.println(orderSn);
        if (userId != null){
           OrderDetailVo orderDetailVo =  orderService.getOrderDetail(userId, orderSn);
           return R.ok().put("data", orderDetailVo);
        }
        return R.unLogin();
    }
}
