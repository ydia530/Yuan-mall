package com.yuan.mall.order.to;

import com.yuan.mall.order.entity.OrderEntity;
import com.yuan.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * <p>Title: OrderCreateTo</p>
 * Description：
 */
@Data
public class OrderCreateTo {

	private OrderEntity order;

	private List<OrderItemEntity> orderItems;

	/**
	 * 订单计算的应付价格
	 */
	private BigDecimal payPrice;

	/**
	 * 运费
	 */
	private BigDecimal fare;
}
