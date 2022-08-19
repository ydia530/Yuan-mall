package com.yuan.mall.ware.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yuan.common.exception.BizCodeEnum;
import com.yuan.common.exception.NotStockException;
import com.yuan.mall.ware.vo.SkuHasStockVo;
import com.yuan.mall.ware.entity.WareSkuEntity;
import com.yuan.mall.ware.vo.WareSkuLockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.mall.ware.service.WareSkuService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 商品库存
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 14:52:55
 */
@RestController
@Slf4j
@RequestMapping("ware/waresku")
public class WareSkuController {


    @Autowired
    private WareSkuService wareSkuService;

    @PostMapping("/lock/order")
    public R orderLockStock(@RequestBody WareSkuLockVo vo){ // SkuId  count
        try {
            Boolean aBoolean = wareSkuService.orderLockStock(vo);
            return R.ok();
        } catch (NotStockException e) {
            log.warn("\n" + e.getMessage());
        }
        return R.error(BizCodeEnum.NOT_STOCK_EXCEPTION.getCode(), BizCodeEnum.NOT_STOCK_EXCEPTION.getMessage());
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = wareSkuService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		WareSkuEntity wareSku = wareSkuService.getBySkuId(id);

        return R.ok().put("wareSku", wareSku);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody WareSkuEntity wareSku){
		wareSkuService.save(wareSku);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody WareSkuEntity wareSku){
		wareSkuService.updateById(wareSku);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		wareSkuService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

    @PostMapping("/hasstock")
    public List<SkuHasStockVo> getSkuHasStock(@RequestBody List<Long> skuIds){
        List<SkuHasStockVo> vos = wareSkuService.getSkuHasStock(skuIds);
        return vos;
    }

}
