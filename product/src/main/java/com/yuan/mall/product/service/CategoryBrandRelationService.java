package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.BrandVo;
import com.yuan.mall.product.entity.BrandEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.CategoryBrandRelationEntity;
import com.yuan.mall.product.entity.BrandEntity;
import com.yuan.mall.product.entity.CategoryBrandRelationEntity;

import java.util.List;
import java.util.Map;

/**
 * 品牌分类关联
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:57
 */
public interface CategoryBrandRelationService extends IService<CategoryBrandRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveDetail(CategoryBrandRelationEntity categoryBrandRelation);

    void updateBrandName(Long brandId, String name);

    void updateCategory(Long catId, String name);

    List<BrandEntity> getBrandRelation(Long catId);
}

