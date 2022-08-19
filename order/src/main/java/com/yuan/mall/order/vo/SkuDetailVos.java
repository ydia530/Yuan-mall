package com.yuan.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/16
 */
@Data
public class SkuDetailVos {

    private String goodsName;

    private String image;

    private BigDecimal payPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     *
     */
    private BigDecimal realSettlePrice;

    /**
     * 库存
     */
    private Integer reminderStock;

    /**
     *
     */
    private BigDecimal settlePrice;

    private Long skuId;

    private List<String> skuSpecLst;

    private Long spuId;

    private BigDecimal totalSkuPrice;
}
