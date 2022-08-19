package com.yuan.mall.cart.service;


import com.yuan.common.utils.R;
import com.yuan.mall.cart.vo.Cart;
import com.yuan.mall.cart.vo.CartItem;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Yuan Diao
 * @date 2022/7/16
 */
public interface CartService{
    Cart getCart(Integer userId);

    R addToCart( Integer userId, Long skuId, Integer num) throws ExecutionException, InterruptedException;

    void selectGood(Integer userId, Long skuId, Boolean isSelect);

    void selectAll(Integer userId, Boolean isSelect);

    void deleteGood(Integer userId, Long skuId);

    void changeQuantity(Integer userId, Long skuId, Integer quantity);

    List<CartItem> getItemsForOrder(Integer userId);
}
