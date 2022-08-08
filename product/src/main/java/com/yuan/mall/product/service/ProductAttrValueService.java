package com.yuan.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.ProductAttrValueEntity;
import com.yuan.mall.product.entity.ProductAttrValueEntity;

import java.util.List;
import java.util.Map;

/**
 * spu属性值
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:57
 */
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveProductAttr(List<ProductAttrValueEntity> collect);

    void updateSpuAttr(Long spuId, List<ProductAttrValueEntity> entities);

    List<ProductAttrValueEntity> baseAttrListForSpu(Long spuId);
}

