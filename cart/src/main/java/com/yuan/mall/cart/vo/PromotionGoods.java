package com.yuan.mall.cart.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/11
 */
@Data
public class PromotionGoods {
    public String title;

    public List<String> tagText;

    public String tag;

    public String promotionSubCode;

    public Integer promotionStatus;

    public Long promotionId;

    public String promotionCode;

    public String isNeedAddOnShop;

    public List<Good> goodsPromotionList;

    public String doorSillRemain;

    public String description;

}
