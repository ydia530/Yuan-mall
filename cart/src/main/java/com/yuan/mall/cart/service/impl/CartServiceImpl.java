package com.yuan.mall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuan.common.utils.R;
import com.yuan.mall.cart.feign.ProductFeignService;
import com.yuan.mall.cart.feign.WareFeignService;
import com.yuan.mall.cart.service.CartService;
import com.yuan.mall.cart.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @author Yuan Diao
 * @date 2022/7/16
 */
@Service
@Slf4j
public class CartServiceImpl implements CartService {

    private final String CART_PREFIX = "cart:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareFeignService wareFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Override
    public Cart getCart(Integer userId) {
        Cart cart = new Cart();
        List<CartItem> cartItems = getCartItems(CART_PREFIX + userId.toString());

        StoreGood storeGood = new StoreGood();
        storeGood.setStoreId(1L);
        storeGood.setStoreName("YuanMall");
        List<StoreGood> storeGoods = new ArrayList<>();
        storeGoods.add(storeGood);

        //TODO 现默认无优惠
        PromotionGoods promotionGoods = new PromotionGoods();
        ArrayList<PromotionGoods> promotionGoods1 = new ArrayList<>();
        promotionGoods1.add(promotionGoods);
        storeGood.setPromotionGoodsList(promotionGoods1);
        //组装商品
        List<Good> goods = cartItems.parallelStream()
                .map(item -> convertGoodVo(item))
                .collect(Collectors.toList());

        cart.setTotalAmount(goods.stream()
                .filter(good -> good.isSelected == true)
                .map(Good::getPrice)
                .reduce(BigDecimal.ZERO,BigDecimal::add));

        cart.setIsAllSelected(goods
                .stream()
                .filter(good -> good.isSelected == false)
                .collect(Collectors.toList()).size() == 0);

        cart.setSelectedGoodsCount(goods
                .stream()
                .filter(good -> good.isSelected == true)
                .collect(Collectors.toList()).size());

        promotionGoods.setGoodsPromotionList(goods);
        cart.setStoreGoods(storeGoods);
        return cart;
    }

    private Good convertGoodVo(CartItem item) {
        Good good = new Good();
        good.setPrice(item.getPrice().multiply(new BigDecimal(item.getCount())));
        good.setSkuId(item.getSkuId());
        good.setPrimaryImage(item.getImage());
        good.setThumb(item.getImage());
        good.setTitle(item.getTitle());
        good.setSelected(item.getCheck());
        good.setQuantity(item.getCount());
        good.setSpecInfo(item.getSkuAttr());
        //库存服务调用
        try{
            R r = wareFeignService.skuInfo(item.getSkuId());
            WareSkuVo wareSku = r.getData("wareSku", new TypeReference<WareSkuVo>() {});

            if (wareSku.getStock() - wareSku.getStockLocked() > 0){
                good.setAvailable(1);
                good.setPutOnSale(1);
                good.setStockQuantity(wareSku.getStock() - wareSku.getStockLocked());
            } else {
                good.setPutOnSale(0);
            }
        }catch (Exception e){
            log.error("服务调用失败 +" + e.toString());
        }


        return good;
    }

    /**
     * 获取购物车所有项
     */
    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> hashOps = stringRedisTemplate.boundHashOps(cartKey);
        List<Object> values = hashOps.values();
        if (values != null && values.size() > 0) {
            return values.stream().map(
                    obj -> JSON.parseObject((String) obj, CartItem.class)).collect(Collectors.toList());
        }

        return null;
    }

    @Override
    public R addToCart(Integer userId, Long skuId, Integer num) throws ExecutionException, InterruptedException {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        // 查看该用户购物车里是否有指定的skuId
        String res = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(res)) {
            CartItem cartItem = new CartItem();
            CompletableFuture<Void> getSkuInfo = CompletableFuture.runAsync(() -> {
                // 1. 远程查询当前要添加的商品的信息
                R skuInfo = productFeignService.SkuInfo(skuId);
                SkuInfoVo sku = skuInfo.getData("skuInfo", new TypeReference<SkuInfoVo>() {});
                // 2. 填充购物项
                cartItem.setSpuId(sku.getSpuId());
                cartItem.setCount(num);
                cartItem.setCheck(true);
                cartItem.setImage(sku.getSkuDefaultImg());
                cartItem.setPrice(sku.getPrice());
                cartItem.setTitle(sku.getSkuTitle());
                cartItem.setSkuId(skuId);
            }, executor);

            // 3. 远程查询sku销售属性，销售属性是个list
            CompletableFuture<Void> getSkuSaleAttrValues = CompletableFuture.runAsync(() -> {
                List<String> values = productFeignService.getSkuSaleAttrValues(skuId);
                cartItem.setSkuAttr(values);
            }, executor);

            // 等待执行完成
            CompletableFuture.allOf(getSkuInfo, getSkuSaleAttrValues).get();

            // sku放到用户购物车redis中
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }  else {
            //购物车里已经有该sku了，数量+1即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            // 不太可能并发，无需加锁
            cartItem.setCount(cartItem.getCount() + num);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }

        return null;
    }

    @Override
    public void selectGood(Integer userId, Long skuId, Boolean isSelect) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        String res = (String) cartOps.get(skuId.toString());
        if (res != null) {
            //购物车里已经有该sku了，数量+1即可
            CartItem cartItem = JSON.parseObject(res, CartItem.class);
            // 不太可能并发，无需加锁
            cartItem.setCheck(isSelect);
            cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
        }
    }

    @Override
    public void selectAll(Integer userId, Boolean isSelect) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        List<CartItem> cartItems = getCartItems(CART_PREFIX + userId.toString());
        if (!CollectionUtils.isEmpty(cartItems)){
            cartItems.parallelStream().forEach(cartItem -> {
                cartItem.setCheck(isSelect);
                cartOps.put(cartItem.getSkuId().toString(), JSON.toJSONString(cartItem));
            });
        }
    }

    @Override
    public void deleteGood(Integer userId, Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        String res = (String) cartOps.get(skuId.toString());
        if (res != null){
            cartOps.delete(skuId.toString());
        }
    }

    @Override
    public void changeQuantity(Integer userId, Long skuId, Integer quantity) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        String res = (String) cartOps.get(skuId.toString());
        if (quantity > 0 ){
            if (res != null){
                CartItem cartItem = JSON.parseObject(res, CartItem.class);
                cartItem.setCount(quantity);
                cartOps.put(skuId.toString(), JSON.toJSONString(cartItem));
            }
        }
    }

    @Override
    public List<CartItem> getItemsForOrder(Integer userId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps(userId);
        List<Object> values = cartOps.values();
        if (values != null && values.size() > 0) {
            return values.stream().map(
                    obj -> JSON.parseObject((String) obj, CartItem.class))
                    .filter(cartItem -> cartItem.getCheck() == true)
                    .collect(Collectors.toList());
        }
        return null;
    }


    /**
     * 用户购物车redis-map
     */
    private BoundHashOperations<String, Object, Object> getCartOps(Integer userId) {
        String cartKey = CART_PREFIX;
        log.debug("\n用户 [" + userId + "] 正在操作购物车");
        cartKey += userId;
        // 绑定这个 key 以后所有对redis 的操作都是针对这个key
        return stringRedisTemplate.boundHashOps(cartKey);
    }
}
