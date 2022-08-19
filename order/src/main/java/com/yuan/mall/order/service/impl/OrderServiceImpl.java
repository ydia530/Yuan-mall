package com.yuan.mall.order.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.constant.OrderStatusEnum;
import com.yuan.common.constant.SubmitOrderStatusEnum;
import com.yuan.common.exception.NotStockException;
import com.yuan.common.to.mq.OrderTo;
import com.yuan.common.utils.R;
import com.yuan.mall.order.constant.OrderConstant;
import com.yuan.mall.order.controller.OrderSubmitRequestVo;
import com.yuan.mall.order.dao.OrderDao;
import com.yuan.mall.order.entity.OrderEntity;
import com.yuan.mall.order.entity.OrderItemEntity;
import com.yuan.mall.order.feign.CartFeignService;
import com.yuan.mall.order.feign.MemberFeignService;
import com.yuan.mall.order.feign.ProductFeignService;
import com.yuan.mall.order.feign.WmsFeignService;
import com.yuan.mall.order.service.OrderItemService;
import com.yuan.mall.order.service.OrderService;
import com.yuan.mall.order.to.OrderCreateTo;
import com.yuan.mall.order.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Yuan Diao
 * @date 2022/8/14
 */
@Slf4j
@Service("orderService")
public class OrderServiceImpl extends ServiceImpl<OrderDao, OrderEntity> implements OrderService {

    @Resource
    private CartFeignService cartFeignService;

    @Resource
    private MemberFeignService memberFeignService;

    @Resource
    private OrderItemService orderItemService;

    @Resource
    private WmsFeignService wmsFeignService;

    @Resource
    private ProductFeignService productFeignService;

    @Resource
    private ThreadPoolExecutor executor;

    @Resource
    private RabbitTemplate rabbitTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private ThreadLocal<OrderSubmitRequestVo> submitRequestVoThreadLocal = new ThreadLocal<>();



    @Override
    public OrderConfirmVo orderConfirm(Integer userId, OrderRequestVo request) throws ExecutionException, InterruptedException {
        // 封装订单
        OrderConfirmVo confirmVo = new OrderConfirmVo();
        // 我们要从request里获取用户数据，但是其他线程是没有这个信息的，
        // 所以可以手动设置新线程里也能共享当前的request数据
        RequestAttributes attributes = RequestContextHolder.getRequestAttributes();

        //1. 获取用户地址
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            RequestContextHolder.setRequestAttributes(attributes);
            R r = memberFeignService.getAddress();
            List<UmsAddressVo> addressVos = r.getData("data", new TypeReference<List<UmsAddressVo>>() {});
            //用户是否已经选择地址
            if (request.getUserAddressReq() != null){
                confirmVo.setUserAddress(addressVos.stream()
                        .filter(addressVo -> addressVo.getId().equals(request.getUserAddressReq()))
                        .findAny().orElse(null));
            } else {
                //返回默认地址
                confirmVo.setUserAddress(addressVos
                        .stream()
                        .filter(addressVo -> addressVo.getIsDefault().equals(1))
                        .findAny()
                        .orElse(null));
            }
            if (confirmVo.getUserAddress() != null){
                confirmVo.setSettleType(1);
                confirmVo.setTotalDeliveryFee(calculateFare(confirmVo.getUserAddress()));
            }
        }, executor);

        //2。获取用户购物车中选中的商品
        CompletableFuture<List<CartItem>> itemFuture = CompletableFuture.supplyAsync(() -> {
            // 因为异步线程需要新的线程，而新的线程里没有request数据，所以我们自己设置进去
            RequestContextHolder.setRequestAttributes(attributes);
            R r = cartFeignService.getCartItemsForOrder();
            List<CartItem> cartItems = r.getData("data", new TypeReference<List<CartItem>>() {});
            return cartItems;
        }, executor);

        //3. 查库存
        CompletableFuture<Void> stockFuture = itemFuture.thenAcceptAsync(res -> {
            Map<Long, CartItem> map = res.stream().collect(Collectors.toMap(cartItem -> cartItem.getSkuId(), cartItem -> cartItem));
            List<SkuHasStockVo> hasStock = wmsFeignService.getSkuHasStock(map.keySet().stream().collect(Collectors.toList()));

            List<SkuDetailVos> collect = hasStock.stream().map(skuHasStockVo -> {
                //todo 库存数是否大于用户购物车商品数量，
                Long skuId = skuHasStockVo.getSkuId();
                CartItem cartItem = map.get(skuId);
                if (skuHasStockVo.getHasStock() >= cartItem.getCount()) {
                    SkuDetailVos skuDetailVos = buildSkuDetialVo(skuHasStockVo, cartItem);
                    System.out.println(skuDetailVos);
                    return skuDetailVos;
                }
                return null;
            }).collect(Collectors.toList());
            List<StoreGoodsVo> storeGoodsVos = new ArrayList<>();
            StoreGoodsVo storeGoodsVo = new StoreGoodsVo();
            storeGoodsVo.setSkuDetailVos(collect);

            //计算商品总价
            confirmVo.setTotalSalePrice(collect.stream()
                    .map(SkuDetailVos::getTotalSkuPrice)
                    .reduce(BigDecimal.ZERO,BigDecimal::add));

            storeGoodsVos.add(storeGoodsVo);

            confirmVo.setTotalGoodsCount(collect.size());
            confirmVo.setTotalPayAmount(calculatePayAmount(confirmVo));
            confirmVo.setStoreGoodsList(storeGoodsVos);
        }, executor);

        // TODO 5.防重令牌 设置用户的令牌
        String token = UUID.randomUUID().toString().replace("-", "");
        confirmVo.setOrderToken(token);
        // redis中添加用户id，这个设置可以防止订单重复提交。生成完一次订单后删除redis
        stringRedisTemplate.opsForValue().set(OrderConstant.USER_ORDER_TOKEN_PREFIX + userId, token, 10, TimeUnit.MINUTES);
        // 等待所有异步任务完成
        CompletableFuture.allOf(addressFuture, itemFuture, stockFuture).get();
        return confirmVo;
    }

    @Override
    @Transactional
//    @GlobalTransactional
    public String submitOrder(Integer userId, OrderSubmitRequestVo request) {
        SubmitOrderResponseVo submitVo = new SubmitOrderResponseVo();
        request.setUserId(userId.longValue());
        submitRequestVoThreadLocal.set(request);

        // 0：正常
        submitVo.setCode(0);
        // 去服务器创建订单,验令牌,验价格,锁库存


        // 1. 验证令牌 [必须保证原子性] 返回 0 or 1
        // 0 令牌删除失败 1删除成功
        String script = "if redis.call('get',KEYS[1]) == ARGV[1]" +
                "then return redis.call('del',KEYS[1]) " +
                "else return 0 " +
                "end";
        String orderToken = request.getToken();

        // 原子验证令牌 删除令牌
        Long result = stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class),
                Arrays.asList(OrderConstant.USER_ORDER_TOKEN_PREFIX + userId),
                orderToken);
        if (result == 0L) { // 令牌验证失败
            submitVo.setCode(SubmitOrderStatusEnum.TOKENERROR.getCode());
        }else{
            // 令牌验证成功
            // 1 .创建订单等信息 // 有生成订单号、计算总价
            OrderCreateTo order = createOrder();
            // 2. 验价
            BigDecimal payAmount = order.getOrder().getPayAmount();
            BigDecimal voPayPrice = request.getTotalAmount();
            if (Math.abs(payAmount.subtract(voPayPrice).doubleValue()) < 0.01) {
                // 金额对比成功
                //库存锁定
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());
                List<OrderItemVo> locks = order.getOrderItems().stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    // 锁定的skuId 这个skuId要锁定的数量
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());

                lockVo.setLocks(locks);
                // 远程锁库存
                R r = wmsFeignService.orderLockStock(lockVo);
                if (r.getCode() == 0) {
                    // 库存足够 锁定成功 给MQ发送订单消息，到时为支付则取消
                    submitVo.setOrderEntity(order.getOrder());
                    saveOrder(order);
                    //TODO 订单创建成功，发送消息给MQ
                    rabbitTemplate.convertAndSend("order-event-exchange","order.create.order",order.getOrder());
                } else {
                    // 锁定失败
                    String msg = (String) r.get("msg");
                    throw new NotStockException(msg);
                }
            } else {

                // 价格验证失败
                submitVo.setCode(SubmitOrderStatusEnum.CHECKPRICE.getCode());
                log.warn("验价失败");
            }
        }
        if (submitVo.getCode() == 0){
            return submitVo.getOrderEntity().getOrderSn();
        }
        return null;
    }

    /**
     * 保存订单所有数据
     */
    private void saveOrder(OrderCreateTo order) {
        OrderEntity orderEntity = order.getOrder();
        orderEntity.setModifyTime(new Date());
        this.save(orderEntity);

        List<OrderItemEntity> orderItems = order.getOrderItems();
        orderItems = orderItems.stream().map(item -> {
            item.setOrderId(orderEntity.getId());
            item.setSpuName(item.getSpuName());
            item.setOrderSn(order.getOrder().getOrderSn());
            return item;
        }).collect(Collectors.toList());
        orderItemService.saveBatch(orderItems);
    }

    /**
     * 创建订单
     */
    private OrderCreateTo createOrder() {

        OrderCreateTo orderCreateTo = new OrderCreateTo();
        // 1. 生成一个订单号
        String orderSn = IdWorker.getIdStr();

        // 填充订单的各种基本信息，价格信息
        OrderEntity orderEntity = buildOrderSn(orderSn);

        // 2. 获取所有订单项   // 从里面已经设置好了用户该使用的价格
        List<OrderItemEntity> items = buildOrderItems(orderSn);

        // 3.根据订单项计算价格	传入订单 、订单项 计算价格、积分、成长值等相关信息
        computerPrice(orderEntity, items);

        orderCreateTo.setOrder(orderEntity);
        orderCreateTo.setOrderItems(items);
        return orderCreateTo;
    }


    /**
     * 验价  传入订单（包含了优惠券等，最终价格也要放到他里面）和订单项
     */
    private void computerPrice(OrderEntity orderEntity, List<OrderItemEntity> items) {

        // 叠加每一个订单项的金额
        BigDecimal coupon = new BigDecimal("0.0");
        BigDecimal integration = new BigDecimal("0.0");
        BigDecimal promotion = new BigDecimal("0.0");
        BigDecimal gift = new BigDecimal("0.0");
        BigDecimal growth = new BigDecimal("0.0");
        BigDecimal total = new BigDecimal("0.0");
        // 总价
        BigDecimal totalPrice = new BigDecimal("0.0");
        for (OrderItemEntity item : items) {  // 这段逻辑不是特别合理，最重要的是累积总价，别的可以跳过
            // 优惠券的金额
            coupon = coupon.add(item.getCouponAmount());
            // 积分优惠的金额
            integration = integration.add(item.getIntegrationAmount());
            // 打折的金额
            promotion = promotion.add(item.getPromotionAmount());
            BigDecimal realAmount = item.getRealAmount();
            totalPrice = totalPrice.add(realAmount);

            // 购物获取的积分、成长值
            gift.add(new BigDecimal(item.getGiftIntegration().toString()));
            growth.add(new BigDecimal(item.getGiftGrowth().toString()));
            total = total.add(item.getSkuPrice());
        }
        // 1.订单价格相关 总额、应付总额
        orderEntity.setTotalAmount(total);
        orderEntity.setPayAmount(totalPrice.add(orderEntity.getFreightAmount()));

        orderEntity.setPromotionAmount(promotion);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setCouponAmount(coupon);

        // 设置积分、成长值
        orderEntity.setIntegration(gift.intValue());
        orderEntity.setGrowth(growth.intValue());

        // 设置订单的删除状态
        orderEntity.setDeleteStatus(OrderStatusEnum.CREATE_NEW.getCode());
    }

    /**
     * 为 orderSn 订单构建订单数据
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {
        // 这里是最后一次来确认购物项的价格 这个远程方法还会查询一次数据库
        R r = cartFeignService.getCartItemsForOrder();
        List<CartItem> cartItems = r.getData("data", new TypeReference<List<CartItem>>() {});
        List<OrderItemEntity> itemEntities = null;
        if (cartItems != null && cartItems.size() > 0) {
            itemEntities = cartItems.stream().map(cartItem -> {
                OrderItemEntity itemEntity = buildOrderItem(cartItem);
                itemEntity.setOrderSn(orderSn);
                return itemEntity;
            }).collect(Collectors.toList());
        }
        return itemEntities;
    }

    private BigDecimal calculatePayAmount(OrderConfirmVo confirmVo) {
        BigDecimal originalTotalPrice  = confirmVo.getTotalSalePrice();

        //TODO 优惠券计算
        BigDecimal totalCouponAmount = confirmVo.getTotalCouponAmount();

        BigDecimal totalDeliveryFee = confirmVo.getTotalDeliveryFee();

        BigDecimal promotionAmount = originalTotalPrice.multiply(new BigDecimal(0.1));
        confirmVo.setTotalPromotionAmount(promotionAmount);
        return originalTotalPrice.subtract(totalCouponAmount).subtract(promotionAmount).add(totalDeliveryFee);
    }

    private SkuDetailVos buildSkuDetialVo(SkuHasStockVo vo, CartItem cartItem) {
        SkuDetailVos skuDetailVos = new SkuDetailVos();
        skuDetailVos.setSkuId(cartItem.getSkuId());
        skuDetailVos.setImage(cartItem.getImage());
        skuDetailVos.setQuantity(cartItem.getCount());
        skuDetailVos.setSkuSpecLst(cartItem.getSkuAttr());
        skuDetailVos.setGoodsName(cartItem.getTitle());
        skuDetailVos.setPayPrice(cartItem.getPrice());
        skuDetailVos.setReminderStock(vo.getHasStock());
        skuDetailVos.setTotalSkuPrice(cartItem.getTotalPrice());
        skuDetailVos.setSettlePrice(cartItem.getPrice());
        return skuDetailVos;
    }


    /**
     * 构建一个订单
     */
    private OrderEntity buildOrderSn(String orderSn) {
        OrderSubmitRequestVo orderSubmitRequestVo = submitRequestVoThreadLocal.get();
        OrderEntity entity = new OrderEntity();
        entity.setOrderSn(orderSn);
        entity.setCreateTime(new Date());
        entity.setCommentTime(new Date());
        entity.setReceiveTime(new Date());
        entity.setDeliveryTime(new Date());
        entity.setMemberId(orderSubmitRequestVo.getUserId());
        entity.setNote(orderSubmitRequestVo.getRemark());
//        entity.setMemberUsername(respVo.getUsername());
//        entity.setBillReceiverEmail(respVo.getEmail());
        // 2. 获取收获地址信息 计算运费
        R addressVo = memberFeignService.getAddressInfo(orderSubmitRequestVo.getUserAddressReq());
        UmsAddressVo resp = addressVo.getData(new TypeReference<UmsAddressVo>() {});
        entity.setFreightAmount(calculateFare(resp));
        entity.setReceiverCity(resp.getCityName());
        entity.setReceiverDetailAddress(resp.getDetailAddress());
        entity.setDeleteStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setReceiverPhone(resp.getPhone());
        entity.setReceiverName(resp.getName());
        entity.setReceiverProvince(resp.getProvinceName());
        entity.setReceiverRegion(resp.getDistrictName());
        // 设置订单为新建状态
        entity.setStatus(OrderStatusEnum.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(7);//自动确认收货
        return entity;
    }

    private BigDecimal calculateFare(UmsAddressVo resp) {
        if (resp != null) {
            // 假设电话后2位为运费
            String phone = resp.getPhone();
            if (phone == null || phone.length() < 2) {
                phone = new Random().nextInt(100) + "";
            }
            return new BigDecimal(phone.substring(phone.length() - 1));
        } else {
            return new BigDecimal("10");
        }
    }


    /**
     * 构建某一个订单项
     */ // OrderServiceImpl
    private OrderItemEntity buildOrderItem(CartItem cartItem) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        // 1.订单信息： 订单号
        // 已经在items里设置了
        // 2.商品spu信息
        Long skuId = cartItem.getSkuId();
        // 远程获取spu的信息
        R r = productFeignService.getSpuInfoBySkuId(skuId);
        SpuInfoVo spuInfo = r.getData(new TypeReference<SpuInfoVo>() {});
        itemEntity.setSpuId(spuInfo.getId());
        itemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        itemEntity.setSpuName(spuInfo.getSpuName());
        itemEntity.setCategoryId(spuInfo.getCatalogId());

        // 3.商品的sku信息
        R sku = productFeignService.getSkuInfo(skuId);
        SkuVo skuItem = sku.getData("skuInfo", new TypeReference<SkuVo>() {});
        itemEntity.setSkuId(cartItem.getSkuId());
        itemEntity.setSkuName(cartItem.getTitle());
        itemEntity.setSkuPic(cartItem.getImage());
        itemEntity.setSkuPrice(skuItem.getPrice());
        // 把一个集合按照指定的字符串进行分割得到一个字符串
        // 属性list生成一个string
        String skuAttr = StringUtils.collectionToDelimitedString(cartItem.getSkuAttr(), ";");
        itemEntity.setSkuAttrsVals(skuAttr);
        itemEntity.setSkuQuantity(cartItem.getCount());
        // 4.积分信息 买的数量越多积分越多 成长值越多
        itemEntity.setGiftGrowth(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());
        itemEntity.setGiftIntegration(cartItem.getPrice().multiply(new BigDecimal(cartItem.getCount())).intValue());

        // 5.订单项的价格信息 优惠金额
        // 促销打折
        itemEntity.setPromotionAmount(itemEntity.getSkuPrice()
                .multiply(new BigDecimal(0.1))
                .multiply(new BigDecimal(itemEntity.getSkuQuantity())));

        itemEntity.setCouponAmount(new BigDecimal("0.0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0.0"));

        // 当前订单项的原价
        BigDecimal orign = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        // 减去各种优惠的价格
        BigDecimal subtract =
                orign.subtract(itemEntity.getCouponAmount())
                        .subtract(itemEntity.getPromotionAmount())
                        .subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(subtract);
        return itemEntity;
    }


    /**
     * 按照订单号获取订单信息
     * @param orderSn
     * @return
     */
    @Override
    public OrderEntity getOrderByOrderSn(String orderSn) {

        OrderEntity orderEntity = this.baseMapper.selectOne(new QueryWrapper<OrderEntity>().eq("order_sn", orderSn));

        return orderEntity;
    }


    /**
     * 关闭订单
     * @param orderEntity
     */
    @Override
    public void closeOrder(OrderEntity orderEntity) {

        //关闭订单之前先查询一下数据库，判断此订单状态是否已支付
        OrderEntity orderInfo = this.getOne(new QueryWrapper<OrderEntity>().
                eq("order_sn",orderEntity.getOrderSn()));

        if (orderInfo != null && orderInfo.getStatus().equals(OrderStatusEnum.CREATE_NEW.getCode())) {
            //代付款状态进行关单
            OrderEntity orderUpdate = new OrderEntity();
            orderUpdate.setId(orderInfo.getId());
            orderUpdate.setStatus(OrderStatusEnum.CANCLED.getCode());
            this.updateById(orderUpdate);

            // 发送消息给MQ
            OrderTo orderTo = new OrderTo();
            BeanUtils.copyProperties(orderInfo, orderTo);

            try {
                //TODO 确保每个消息发送成功，给每个消息做好日志记录，(给数据库保存每一个详细信息)保存每个消息的详细信息
                rabbitTemplate.convertAndSend("order-event-exchange", "order.release.other", orderTo);
            } catch (Exception e) {
                //TODO 定期扫描数据库，重新发送失败的消息
            }
        }
    }

    @Override
    public OrderDetailVo getOrderDetail(Integer userId, String orderSn) {
        OrderDetailVo orderDetailVo = new OrderDetailVo();

        OrderEntity order = this.getOrderByOrderSn(orderSn);
        if (order != null && order.getMemberId().equals(userId)){
            return null;
        }
        // 1 组装收货信息
        LogisticsVO logisticsVO = constructLogisticsVo(order);
        orderDetailVo.setLogisticsVO(logisticsVO);

        // 2 拼装购物项
        orderDetailVo.setOrderItemVOs(constructOrderItem(orderSn));
        orderDetailVo.setRemark(order.getNote());
        orderDetailVo.setOrderId(orderDetailVo.getOrderId());
        orderDetailVo.setOrderNo(orderSn);
        orderDetailVo.setOrderStatus(order.getStatus());
        orderDetailVo.setOrderStatusName(OrderStatusEnum.getByCode(order.getStatus()).getMsg());
        orderDetailVo.setCreateTime(order.getCreateTime().getTime());
        orderDetailVo.setPaymentAmount(order.getPayAmount());
        orderDetailVo.setGoodsAmountApp(order.getTotalAmount());
        orderDetailVo.setTotalAmount(order.getTotalAmount());
        orderDetailVo.setDiscountAmount(order.getPromotionAmount());
        orderDetailVo.setCouponAmount(order.getCouponAmount());
        orderDetailVo.setFreightFee(order.getFreightAmount());

        Calendar Cal=java.util.Calendar.getInstance();
        Cal.setTime(order.getCreateTime());
        Cal.add(Calendar.MINUTE,1);
        orderDetailVo.setAutoCancelTime(Cal.getTime().getTime() - System.currentTimeMillis());
        orderDetailVo.setButtonVOs(constructButtonVos(order.getStatus()));
        return orderDetailVo;
    }

    private List<ButtonVos> constructButtonVos(Integer status) {
        List<ButtonVos> buttonVos = new ArrayList<>();
        switch (status){
            case 0:
                buttonVos.add(new ButtonVos(false, 2, "取消订单"));
                buttonVos.add(new ButtonVos(true, 1, "付款"));
        }
        return buttonVos;
    }

    private List<OrderItemEntity> constructOrderItem(String orderSn) {
        QueryWrapper<OrderItemEntity> orderItemEntityQueryWrapper = new QueryWrapper<>();
        orderItemEntityQueryWrapper.eq("order_sn", orderSn);
        List<OrderItemEntity> orderItemEntities = orderItemService.getBaseMapper().selectList(orderItemEntityQueryWrapper);
        return orderItemEntities;
    }

    private LogisticsVO constructLogisticsVo(OrderEntity order) {
        LogisticsVO logisticsVO = new LogisticsVO();
        logisticsVO.setReceiverCity(order.getReceiverCity());
        logisticsVO.setReceiverProvince(order.getReceiverProvince());
        logisticsVO.setReceiverCountry(order.getReceiverRegion());
        logisticsVO.setReceiverAddress(order.getReceiverDetailAddress());
        logisticsVO.setReceiverName(order.getReceiverName());
        logisticsVO.setReceiverPhone(order.getReceiverPhone());
        return logisticsVO;
    }

}
