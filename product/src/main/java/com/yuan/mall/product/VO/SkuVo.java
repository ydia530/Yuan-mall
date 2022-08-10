package com.yuan.mall.product.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/9
 */
@Data
public class SkuVo {
    public BigDecimal priceInfo;

    public Long skuId;

    public String skuImage;

    public List<SpecInfoVo> specInfo;

    public Long stockQuantity;
}
