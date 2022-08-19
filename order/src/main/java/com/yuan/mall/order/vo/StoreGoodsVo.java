package com.yuan.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/16
 */
@Data
public class StoreGoodsVo{

    /** 优惠券列表 */
    private List<Long> couponList;

    private BigDecimal deliveryFee = new BigDecimal(0);

    private Integer goodsCount;

    private List<SkuDetailVos> skuDetailVos;

    private Integer storeId = 1;

    private String storeName = "YuanMall";

    private BigDecimal storeTotalAmount;

    private BigDecimal storeTotalCouponAmount;

    private BigDecimal storeTotalDiscountAmount;

    private BigDecimal storeTotalPayAmount;

}
