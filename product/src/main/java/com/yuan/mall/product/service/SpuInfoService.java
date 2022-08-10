package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.SpuSaveVo;
import com.yuan.mall.product.VO.SpuVo;
import com.yuan.mall.product.entity.SpuInfoDescEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.SpuInfoEntity;
import com.yuan.mall.product.VO.SpuSaveVo;
import com.yuan.mall.product.entity.SpuInfoEntity;

import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * spu信息
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:56
 */
public interface SpuInfoService extends IService<SpuInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void saveSupInfo(SpuSaveVo spuInfo);

    void saveBaseSpuInfo(SpuInfoEntity spuInfoEntity);

    PageUtils queryPageByCondition(Map<String, Object> params);

    void up(Long spuId);

    SpuVo getSpuItemInfo(Long spuId) throws ExecutionException, InterruptedException;
}

