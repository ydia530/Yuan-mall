package com.yuan.mall.order.vo;

import com.yuan.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/18
 */
@Data
public class OrderDetailVo {
    private Long orderId;

    private String orderNo;

    private String parentOrderNo;

    private Long storeId = 1L;

    private String storeName = "YuanMall";

    private Integer orderStatus;

    private String orderStatusName;

    /**
     * 应付金额
     */
    private BigDecimal paymentAmount;

    /**
     * 原价
     */
    private BigDecimal goodsAmountApp;
    private BigDecimal totalAmount;

    /**
     * 运费
     */
    private BigDecimal freightFee;

    /**
     * 活动优惠
     */
    private BigDecimal discountAmount;

    private BigDecimal couponAmount;

    private LogisticsVO logisticsVO;

    private List<OrderItemEntity> orderItemVOs;

    private List<ButtonVos> buttonVOs;

    private Long createTime;

    private Long autoCancelTime;

    private String remark;



}
