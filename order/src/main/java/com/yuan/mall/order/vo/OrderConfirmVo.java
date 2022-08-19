package com.yuan.mall.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * <p>Title: OrderConfirmVo</p>
 * Description：订单确认页需要的数据
 * date：2020/6/30 16:20
 */
@Data
public class OrderConfirmVo {

	/**
	 * 所有选中的购物项
	 */
	List<StoreGoodsVo> storeGoodsList;

	/**
	 * 积分信息
	 */
	private Integer integration;

	/**
	 * 防重令牌
	 */
	private String orderToken;


	private UmsAddressVo userAddress;

	/**
	 * 商品总金额
	 */
	private BigDecimal totalSalePrice;

	/**
	 * 商品数量
	 */
	private Integer totalGoodsCount;

	/**
	 * 应支付金额
	 */
	private BigDecimal totalPayAmount;

	/**
	 * 优惠券减免
	 */
	private BigDecimal totalCouponAmount = new BigDecimal(0);

	/**
	 * 店铺促销优惠
	 */
	private BigDecimal totalPromotionAmount;

	/**
	 * 运费
	 */
	private BigDecimal totalDeliveryFee;

	/**
	 * 0 -> 未选择收货地址。 1 -> 已选择收货地址
	 */
	private Integer settleType = 0;
}
