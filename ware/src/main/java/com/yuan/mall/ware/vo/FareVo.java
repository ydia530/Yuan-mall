package com.yuan.mall.ware.vo;

import lombok.Data;

import java.math.BigDecimal;

/**
 * <p>Title: FareVo</p>
 * Description：
 * date：2020/7/1 20:46
 */
@Data
public class FareVo {

	// 地址
	private MemberAddressVo memberAddressVo;

	// 运费
	private BigDecimal fare;
}
