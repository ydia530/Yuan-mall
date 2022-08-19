package com.yuan.mall.product.controller;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.yuan.mall.product.VO.SpuSaveVo;
import com.yuan.mall.product.VO.SpuSaveVo;
import com.yuan.mall.product.VO.SpuVo;
import com.yuan.mall.product.entity.SpuInfoEntity;
import com.yuan.mall.product.service.SpuInfoService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.mall.product.entity.SpuInfoEntity;
import com.yuan.mall.product.service.SpuInfoService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * spu信息
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:56
 */
@RestController
@RequestMapping("/product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);
        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody SpuSaveVo spuInfo){
        spuInfoService.saveSupInfo(spuInfo);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    /**
     * 商品上架
     */
    @PostMapping("/{spuId}/up")
    public R up(@PathVariable("spuId") Long spuId){

        spuInfoService.up(spuId);
        return R.ok();
    }

    @GetMapping("/item")
    public R getSpuItemInfo(@RequestParam Long spuId) throws ExecutionException, InterruptedException {
        if (spuId != null){
            SpuVo spuVo = spuInfoService.getSpuItemInfo(spuId);
            return R.ok().put("data", spuVo);
        }
        return null;
    }

    @GetMapping("/skuId/{id}")
    public R getSpuInfoBySkuId(@PathVariable("id") Long skuId){

        SpuInfoEntity entity = spuInfoService.getSpuInfoBySkuId(skuId);
        return R.ok().setData(entity);
    }

}
