package com.yuan.mall.product.service.impl;

import com.yuan.mall.product.service.CategoryBrandRelationService;
import com.yuan.mall.product.dao.BrandDao;
import com.yuan.mall.product.entity.BrandEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.product.dao.BrandDao;
import com.yuan.mall.product.entity.BrandEntity;
import com.yuan.mall.product.service.BrandService;


@Service("brandService")
public class BrandServiceImpl extends ServiceImpl<BrandDao, BrandEntity> implements BrandService {

    @Autowired
    private CategoryBrandRelationService categoryBrandRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        String keyword = (String) params.get("key");
        QueryWrapper<BrandEntity> queryWrapper = new QueryWrapper();
        if (StringUtils.isNotEmpty(keyword)){
            queryWrapper.eq("brand_id", keyword).or().like("name", keyword);
        }

        IPage<BrandEntity> page = this.page(
                new Query<BrandEntity>().getPage(params),
                queryWrapper
        );

        return new PageUtils(page);
    }

    @Override
    public void updateByDetails(BrandEntity brand) {
        this.updateById(brand);
        categoryBrandRelationService.updateBrandName(brand.getBrandId(), brand.getName());
    }

}
