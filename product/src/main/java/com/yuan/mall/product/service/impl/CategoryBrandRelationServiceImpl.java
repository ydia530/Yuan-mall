package com.yuan.mall.product.service.impl;

import com.yuan.mall.product.dao.BrandDao;
import com.yuan.mall.product.dao.CategoryDao;
import com.yuan.mall.product.entity.BrandEntity;
import com.yuan.mall.product.service.BrandService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.yuan.mall.product.dao.BrandDao;
import com.yuan.mall.product.dao.CategoryBrandRelationDao;
import com.yuan.mall.product.dao.CategoryDao;
import com.yuan.mall.product.entity.BrandEntity;
import com.yuan.mall.product.entity.CategoryBrandRelationEntity;
import com.yuan.mall.product.service.BrandService;
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

import com.yuan.mall.product.dao.CategoryBrandRelationDao;
import com.yuan.mall.product.entity.CategoryBrandRelationEntity;
import com.yuan.mall.product.service.CategoryBrandRelationService;
import org.springframework.transaction.annotation.Transactional;


@Service("categoryBrandRelationService")
public class CategoryBrandRelationServiceImpl extends ServiceImpl<CategoryBrandRelationDao, CategoryBrandRelationEntity> implements CategoryBrandRelationService {

    @Autowired
    private CategoryDao categoryDao;

    @Autowired
    private BrandDao brandDao;

    @Autowired
    private BrandService brandService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<CategoryBrandRelationEntity> page = this.page(
                new Query<CategoryBrandRelationEntity>().getPage(params),
                new QueryWrapper<CategoryBrandRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveDetail(CategoryBrandRelationEntity categoryBrandRelation) {
        Long categoryId = categoryBrandRelation.getCatelogId();
        Long brandId = categoryBrandRelation.getBrandId();

        String categoryName = categoryDao.selectById(categoryId).getName();

        String brandName = brandDao.selectById(brandId).getName();

        categoryBrandRelation.setBrandName(brandName);
        categoryBrandRelation.setCatelogName(categoryName);
        this.save(categoryBrandRelation);
    }

    @Override
    public void updateBrandName(Long brandId, String name) {
        CategoryBrandRelationEntity categoryBrandRelationEntity = new CategoryBrandRelationEntity();
        categoryBrandRelationEntity.setBrandId(brandId);
        categoryBrandRelationEntity.setBrandName(name);
        UpdateWrapper updateWrapper = new UpdateWrapper();
        updateWrapper.eq("brand_id", brandId);
        this.update(categoryBrandRelationEntity, updateWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCategory(Long catId, String name) {
        this.baseMapper.updateCategory(catId, name);
    }

    @Override
    public List<BrandEntity> getBrandRelation(Long catId) {
        List<CategoryBrandRelationEntity> entities = baseMapper.selectList(new QueryWrapper<CategoryBrandRelationEntity>().eq("catelog_id", catId));
        List<BrandEntity> collect = entities.stream().map(item -> {
            Long brandId = item.getBrandId();
            return brandService.getById(brandId);
        }).collect(Collectors.toList());
        return collect;

    }

}
