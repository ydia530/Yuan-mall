package com.yuan.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.BrandEntity;
import com.yuan.mall.product.entity.BrandEntity;

import java.util.Map;

/**
 * 品牌
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
public interface BrandService extends IService<BrandEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void updateByDetails(BrandEntity brand);
}

