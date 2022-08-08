package com.yuan.mall.product.VO;

import lombok.Data;

/**
 * @author Yuan Diao
 * @date 2022/2/2
 */
@Data
public class SkuHasStockVo {
    private Long skuId;

    private Boolean hasStock;
}
