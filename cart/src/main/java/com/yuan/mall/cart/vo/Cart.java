package com.yuan.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/7/16
 */
@Data
public class Cart {

    private List<StoreGood> storeGoods;

    /*** 商品的数量*/
    private Integer selectedGoodsCount;


    /*** 整个购物车的总价*/
    private BigDecimal totalAmount;

    /*** 减免的价格*/
    private BigDecimal totalDiscountAmount = new BigDecimal("0.00");

    private Boolean isAllSelected;

//    /*** 计算商品的总量*/
//    public Integer getCountNum() {
//        int count = 0;
//        if(storeGoods != null && storeGoods.size() > 0){
//            for (CartItem item : this.storeGoods) {
//                count += item.getCount();
//            }
//        }
//        return count;
//    }
//
//
//    public BigDecimal getTotalAmount() {
//        BigDecimal amount = new BigDecimal("0");
//        if(this.storeGoods != null && this.storeGoods.size() > 0){
//            for (CartItem item : this.storeGoods) {
//                if(item.getCheck()){
//                    BigDecimal totalPrice = item.getTotalPrice();
//                    amount = amount.add(totalPrice);
//                }
//            }
//        }
//        return amount.subtract(this.getTotalDiscountAmount());
//    }
}
