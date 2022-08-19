package com.yuan.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/11
 */
@Data
public class Good {
    public Integer available;

    public String etitle;

    public boolean isSelected;

    public BigDecimal price;

    public String primaryImage;

    public Integer putOnSale;

    public Integer quantity;

    public Long skuId;

    public List<String> specInfo;

    public Long spuId;

    public Integer stockQuantity;

    public Boolean stockStatus;

    public String thumb;

    public String title;
}
