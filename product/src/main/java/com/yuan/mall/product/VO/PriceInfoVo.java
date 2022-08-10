package com.yuan.mall.product.VO;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author Yuan Diao
 * @date 2022/8/9
 */
@Data
public class PriceInfoVo {
    public BigDecimal price;
    public Integer priceType;
}
