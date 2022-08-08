package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.ItemSaleAttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.SkuSaleAttrValueEntity;
import com.yuan.mall.product.VO.ItemSaleAttrVo;
import com.yuan.mall.product.entity.SkuSaleAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * sku销售属性&值
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:56
 */
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    List<ItemSaleAttrVo> getSaleAttrsBuSpuId(Long catelogId);

    List<String> getSkuSaleAttrValuesAsStringList(Long skuId);
}

