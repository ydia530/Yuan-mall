package com.yuan.mall.cart.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/11
 */
@Data
public class StoreGood {
   public List<PromotionGoods> promotionGoodsList;
   public Long storeId;
   public String storeName;
}
