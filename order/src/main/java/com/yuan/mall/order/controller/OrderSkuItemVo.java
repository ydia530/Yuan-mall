package com.yuan.mall.order.controller;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yuan Diao
 * @date 2022/8/17
 */
@Data
public class OrderSkuItemVo {
    private Long skuId;

    private Integer quantity;
}
