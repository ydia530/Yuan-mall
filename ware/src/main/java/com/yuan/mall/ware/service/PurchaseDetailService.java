package com.yuan.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.ware.entity.PurchaseDetailEntity;

import java.util.Map;

/**
 *
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 14:52:54
 */
public interface PurchaseDetailService extends IService<PurchaseDetailEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

