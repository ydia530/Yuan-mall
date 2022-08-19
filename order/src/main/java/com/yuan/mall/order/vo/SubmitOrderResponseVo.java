package com.yuan.mall.order.vo;

import com.yuan.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * <p>Title: SubmitOrderResponseVo</p>
 * Description：
 * date：2020/7/1 22:50
 */
@Data
public class SubmitOrderResponseVo {

	private OrderEntity orderEntity;

	/**
	 * 错误状态码：
	 * 0 成功
	 * 1 库存不足
	 * 2 验证失败
	 */
	private Integer code;
}
