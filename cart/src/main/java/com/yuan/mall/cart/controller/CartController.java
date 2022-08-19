package com.yuan.mall.cart.controller;

import com.yuan.common.utils.R;
import com.yuan.mall.cart.annotation.LoginUser;
import com.yuan.mall.cart.service.CartService;
import com.yuan.mall.cart.vo.Cart;
import com.yuan.mall.cart.vo.CartItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @author Yuan Diao
 * @date 2022/7/16
 */
@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;


    @GetMapping("/add_to_cart")
    public R addToCart(@LoginUser Integer userId, @RequestParam Long skuId,
                       @RequestParam Integer num) throws ExecutionException, InterruptedException {
        if (userId != null){
            cartService.addToCart(userId, skuId, num);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/")
    public R getCart(@LoginUser Integer userId) throws ExecutionException, InterruptedException {
        if (userId != null){
            Cart cart = cartService.getCart(userId);
            return R.ok().put("data", cart);
        }
        return R.unLogin();
    }

    @GetMapping("/select_good")
    public R selectGood(@LoginUser Integer userId, @RequestParam Long skuId,
                        @RequestParam Boolean isSelect) {
        if (userId != null){
            cartService.selectGood(userId, skuId, isSelect);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/select_all")
    public R selectAll(@LoginUser Integer userId,
                        @RequestParam Boolean isSelect){
        if (userId != null){
            cartService.selectAll(userId, isSelect);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/delete")
    public R deleteGood(@LoginUser Integer userId,
                        @RequestParam Long skuId){
        if (userId != null){
            cartService.deleteGood(userId, skuId);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/change_quantity")
    public R changeQuantity(@LoginUser Integer userId,
                            @RequestParam Long skuId,
                            @RequestParam Integer quantity){
        if (userId != null){
            cartService.changeQuantity(userId, skuId, quantity);
            return R.ok();
        }
        return R.unLogin();
    }


    @GetMapping("/items_order")
    public R getItemsForOrder(@LoginUser Integer userId){
        if (userId != null){
            List<CartItem> cartItems = cartService.getItemsForOrder(userId);
            return R.ok().put("data", cartItems);
        }
        return R.unLogin();
    }


}
