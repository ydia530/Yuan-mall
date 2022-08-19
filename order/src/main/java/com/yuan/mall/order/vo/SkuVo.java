package com.yuan.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/9
 */
@Data
public class SkuVo {
    public BigDecimal price;

    public Long skuId;

    public String skuImage;

    public Long stockQuantity;

    private String skuName;
}
