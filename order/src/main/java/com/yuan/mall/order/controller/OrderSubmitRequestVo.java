package com.yuan.mall.order.controller;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/17
 */
@Data
public class OrderSubmitRequestVo {

    private List<Long> couponList;

    private String remark;

    private Integer userAddressReq;

    private String token;

    private Long userId;

    private BigDecimal totalAmount;
}
