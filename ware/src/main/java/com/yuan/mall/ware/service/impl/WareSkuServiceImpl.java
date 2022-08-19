package com.yuan.mall.ware.service.impl;

import com.alibaba.fastjson.TypeReference;
import com.yuan.common.exception.NotStockException;
import com.yuan.common.to.mq.OrderTo;
import com.yuan.common.to.mq.StockDetailTo;
import com.yuan.common.to.mq.StockLockedTo;
import com.yuan.common.utils.R;
import com.yuan.mall.ware.entity.WareOrderTaskDetailEntity;
import com.yuan.mall.ware.entity.WareOrderTaskEntity;
import com.yuan.mall.ware.feign.OrderFeignService;
import com.yuan.mall.ware.feign.ProductFeignService;
import com.yuan.mall.ware.service.WareOrderTaskDetailService;
import com.yuan.mall.ware.service.WareOrderTaskService;
import com.yuan.mall.ware.vo.OrderItemVo;
import com.yuan.mall.ware.vo.OrderVo;
import com.yuan.mall.ware.vo.SkuHasStockVo;
import com.yuan.mall.ware.dao.WareSkuDao;
import com.yuan.mall.ware.vo.WareSkuLockVo;
import lombok.Data;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.ware.entity.WareSkuEntity;
import com.yuan.mall.ware.service.WareSkuService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;


@Service("wareSkuService")
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {


    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private WareSkuDao wareSkuDao;

    @Autowired
    private WareOrderTaskService orderTaskService;

    @Autowired
    private WareOrderTaskDetailService wareOrderTaskDetailService;

    @Autowired
    private OrderFeignService orderFeignService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 商品库存的模糊查询
     * skuId: 1
     * wareId: 1
     */
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        QueryWrapper<WareSkuEntity> wrapper = new QueryWrapper<>();
        String id = (String) params.get("skuId");
        if (!StringUtils.isEmpty(id)) {
            wrapper.eq("sku_id", id);
        }
        id = (String) params.get("wareId");
        if (!StringUtils.isEmpty(id)) {
            wrapper.eq("ware_id", id);
        }
        IPage<WareSkuEntity> page = this.page(
                new Query<WareSkuEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


    /**
     * 添加库存
     * wareId: 仓库id
     * return 返回商品价格
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public double addStock(Long skuId, Long wareId, Integer skuNum) {
        // 1.如果还没有这个库存记录 那就是新增操作
        List<WareSkuEntity> entities = baseMapper.selectList(new QueryWrapper<WareSkuEntity>().eq("sku_id", skuId).eq("ware_id", wareId));
        double price = 0.0;
        // TODO 还可以用什么办法让异常出现以后不回滚？补偿机制和消息处理补偿
        WareSkuEntity entity = new WareSkuEntity();
        try {
            R info = productFeignService.info(skuId);
            Map<String, Object> data = (Map<String, Object>) info.get("skuInfo");

            if (info.getCode() == 0) {
                entity.setSkuName((String) data.get("skuName"));
                // 设置商品价格
                price = (Double) data.get("price");
            }
        } catch (Exception e) {
            System.out.println(e);
            System.out.println("ware.service.impl.WareSkuServiceImpl：远程调用出错，后面这里可以用消息队列写一致性事务");
        }
        // 新增操作
        if (entities == null || entities.size() == 0) {
            entity.setSkuId(skuId);
            entity.setStock(skuNum);
            entity.setWareId(wareId);
            entity.setStockLocked(0);
            entity.setSkuName(entity.getSkuName());
            this.baseMapper.insert(entity);
        } else {
            wareSkuDao.addStock(skuId, wareId, skuNum);
        }
        return price;
    }

    /**
     * 这里存过库存数量
     * SELECT SUM(stock - stock_locked) FROM `wms_ware_sku` WHERE sku_id = 1
     */
    @Override
    public List<SkuHasStockVo> getSkuHasStock(List<Long> skuIds) {
        return skuIds.stream().map(id -> {
            SkuHasStockVo stockVo = new SkuHasStockVo();
            // 查询当前sku的总库存量
            stockVo.setSkuId(id);
            // 这里库存可能为null 要避免空指针异常
            Long stock = baseMapper.getSkuStock(id);
            stockVo.setHasStock(stock == null ? 0 : stock);
            return stockVo;
        }).collect(Collectors.toList());
    }

    @Override
    public WareSkuEntity getBySkuId(Long id) {
        QueryWrapper<WareSkuEntity> wareSkuEntityQueryWrapper = new QueryWrapper<>();
        wareSkuEntityQueryWrapper.eq("sku_id", id);
        return baseMapper.selectOne(wareSkuEntityQueryWrapper);
    }


    /**
     *    锁库存
     * 1 下订单成功，订单国旗没有支付被系统自动取消 或 被用户手动取消
     * 2 下订单成功，库存锁定成功，但由于下单流程中其他业务调用失败，导致订单回滚
     * 以上场景需要将之前苏定的库存解锁
     * */
    @Transactional(rollbackFor = NotStockException.class) // 自定义异常
    @Override
    public boolean orderLockStock(WareSkuLockVo vo) { // List<LockStockResult>

        // 锁库存之前先保存订单 以便后来消息撤回
        WareOrderTaskEntity taskEntity = new WareOrderTaskEntity();
        taskEntity.setOrderSn(vo.getOrderSn());
        orderTaskService.save(taskEntity);

        // [理论上]1. 按照下单的收获地址 找到一个就近仓库, 锁定库存
        // [实际上]1. 找到每一个商品在哪一个仓库有库存
        List<OrderItemVo> locks = vo.getLocks();//订单项
        List<SkuWareHasStock> lockVOs = locks.stream().map(item -> {
            // 创建订单项
            SkuWareHasStock hasStock = new SkuWareHasStock();
            Long skuId = item.getSkuId();
            hasStock.setSkuId(skuId);
            // 查询本商品在哪有库存
            List<Long> wareIds = wareSkuDao.listWareIdHasSkuStock(skuId);
            hasStock.setWareId(wareIds);
            hasStock.setNum(item.getCount());//购买数量
            return hasStock;
        }).collect(Collectors.toList());

        for (SkuWareHasStock hasStock : lockVOs) {
            Boolean skuStocked = true;
            Long skuId = hasStock.getSkuId();
            List<Long> wareIds = hasStock.getWareId();
            if (wareIds == null || wareIds.size() == 0) {
                // 没有任何仓库有这个库存（注意可能会回滚之前的订单项，没关系）
                throw new NotStockException(skuId.toString());
            }
            // 1 如果每一个商品都锁定成功 将当前商品锁定了几件的工作单记录发送给MQ
            // 2 如果锁定失败 前面保存的工作单信息回滚了(发送了消息却回滚库存的情况，没关系，用数据库id查就可以)
            for (Long wareId : wareIds) {
                // 成功就返回 1 失败返回0  （有上下限）
                Long count = wareSkuDao.lockSkuStock(skuId, wareId, hasStock.getNum());
                // UPDATE返回0代表失败
                if (count == 0) { // 没有更新对，说明锁当前库库存失败，去尝试其他库
                    skuStocked = false;
                } else { // 即1
                    // TODO 告诉MQ库存锁定成功 一个订单锁定成功 消息队列就会有一个消息
                    // 订单项详情
                    WareOrderTaskDetailEntity detailEntity =
                            new WareOrderTaskDetailEntity(null, skuId, "", hasStock.getNum(), taskEntity.getId(),
                                    wareId, // 锁定的仓库号
                                    1);
                    // db保存订单sku项工作单详情，告诉商品锁的哪个库存
                    wareOrderTaskDetailService.save(detailEntity);
                    // 发送库存锁定消息到延迟队列
                    // 要发送的内容
                    StockLockedTo lockedTo = new StockLockedTo();
                    lockedTo.setId(taskEntity.getId());
                    StockDetailTo detailTo = new StockDetailTo();
                    BeanUtils.copyProperties(detailEntity,detailTo);
                    lockedTo.setDetailTo(detailTo);
                    rabbitTemplate.convertAndSend("stock-event-exchange","stock.locked",lockedTo);
                    break;
                }

            }
            if (!skuStocked) {
                // 当前商品在所有仓库都没锁柱
                throw new NotStockException(skuId.toString());
            }
        }
        // 3.全部锁定成功
        return true;
    }



    @Override
    public void unlockStock(StockLockedTo to) {
        //库存工作单的id
        StockDetailTo detail = to.getDetailTo();
        Long detailId = detail.getId();

        /**
         * 解锁
         * 1、查询数据库关于这个订单锁定库存信息
         *   有：证明库存锁定成功了
         *      解锁：订单状况
         *          1、没有这个订单，必须解锁库存
         *          2、有这个订单，不一定解锁库存
         *              订单状态：已取消：解锁库存
         *                      已支付：不能解锁库存
         */
        WareOrderTaskDetailEntity taskDetailInfo = wareOrderTaskDetailService.getById(detailId);
        if (taskDetailInfo != null) {
            //查出wms_ware_order_task工作单的信息
            Long id = to.getId();
            WareOrderTaskEntity orderTaskInfo = orderTaskService.getById(id);
            //获取订单号查询订单状态
            String orderSn = orderTaskInfo.getOrderSn();
            //远程查询订单信息
            R orderData = orderFeignService.getOrderStatus(orderSn);
            if (orderData.getCode() == 0) {
                //订单数据返回成功
                OrderVo orderInfo = orderData.getData("data", new TypeReference<OrderVo>() {});

                //判断订单状态是否已取消或者支付或者订单不存在
                if (orderInfo == null || orderInfo.getStatus() == 4) {
                    //订单已被取消，才能解锁库存
                    if (taskDetailInfo.getLockStatus() == 1) {
                        //当前库存工作单详情状态1，已锁定，但是未解锁才可以解锁
                        unLockStock(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detailId);
                    }
                }
            } else {
                //消息拒绝以后重新放在队列里面，让别人继续消费解锁
                //远程调用服务失败
                throw new RuntimeException("远程调用服务失败");
            }
        } else {
            //无需解锁
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直改不了，库存优先到期，查订单状态新建，什么都不处理
     * 导致卡顿的订单，永远都不能解锁库存
     * @param orderTo
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public void unlockStock(OrderTo orderTo) {

        String orderSn = orderTo.getOrderSn();
        //查一下最新的库存解锁状态，防止重复解锁库存
        WareOrderTaskEntity orderTaskEntity = orderTaskService.getOrderTaskByOrderSn(orderSn);

        //按照工作单的id找到所有 没有解锁的库存，进行解锁
        Long id = orderTaskEntity.getId();
        List<WareOrderTaskDetailEntity> list = wareOrderTaskDetailService.list(new QueryWrapper<WareOrderTaskDetailEntity>()
                .eq("task_id", id).eq("lock_status", 1));

        for (WareOrderTaskDetailEntity taskDetailEntity : list) {
            unLockStock(taskDetailEntity.getSkuId(),
                    taskDetailEntity.getWareId(),
                    taskDetailEntity.getSkuNum(),
                    taskDetailEntity.getId());
        }

    }

    /**
     * 解锁库存的方法
     * @param skuId
     * @param wareId
     * @param num
     * @param taskDetailId
     */
    public void unLockStock(Long skuId,Long wareId,Integer num,Long taskDetailId) {

        //库存解锁
        wareSkuDao.unLockStock(skuId,wareId,num);

        //更新工作单的状态
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(taskDetailId);
        //变为已解锁
        taskDetailEntity.setLockStatus(2);
        wareOrderTaskDetailService.updateById(taskDetailEntity);

    }


    @Data
    class SkuWareHasStock {

        private Long skuId;

        private List<Long> wareId;

        private Integer num;
    }
}
