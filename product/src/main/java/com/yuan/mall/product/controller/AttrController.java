package com.yuan.mall.product.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yuan.mall.product.VO.AttrResponseVo;
import com.yuan.mall.product.VO.AttrVo;
import com.yuan.mall.product.entity.ProductAttrValueEntity;
import com.yuan.mall.product.service.ProductAttrValueService;
import com.yuan.mall.product.VO.AttrResponseVo;
import com.yuan.mall.product.VO.AttrVo;
import com.yuan.mall.product.entity.ProductAttrValueEntity;
import com.yuan.mall.product.service.AttrService;
import com.yuan.mall.product.service.ProductAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.mall.product.service.AttrService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 商品属性
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
@RestController
@RequestMapping("product/attr")
public class AttrController {
    @Autowired
    private AttrService attrService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    /**
     * 列表
     */
    @GetMapping("/{attrType}/list/{id}")
    public R list(@PathVariable("id") Long id, @RequestParam Map<String, Object> params, @PathVariable("attrType") String attrType){
        PageUtils page = attrService.queryPage(id, params, attrType);

        return R.ok().put("page", page);
    }

    @GetMapping("/base/listforspu/{spuId}")
    public R baseAttrListForSpu(@PathVariable("spuId") Long spuId){
        List<ProductAttrValueEntity> entities = productAttrValueService.baseAttrListForSpu(spuId);
        return R.ok().put("data", entities);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{attrId}")
    public R info(@PathVariable("attrId") Long attrId){
//		AttrEntity attr = attrService.getById(attrId);
        AttrResponseVo attr = attrService.getInfoById(attrId);
        return R.ok().put("data", attr);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody AttrVo attr){
//		attrService.save(attr);
        attrService.saveAttr(attr);
        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody AttrVo attr){
		attrService.updateInfo(attr);

        return R.ok();
    }


    @PostMapping("/update/{spuId}")
    public R updateSpuAttr(@PathVariable("spuId") Long spuId, @RequestBody List<ProductAttrValueEntity> entities){
        productAttrValueService.updateSpuAttr(spuId, entities);
        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] attrIds){
		attrService.deleteAttr(Arrays.asList(attrIds));
        return R.ok();
    }

}
