package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.AttrGroupWIthAttrsVo;
import com.yuan.mall.product.VO.SpuItemAttrGroup;
import com.yuan.mall.product.entity.AttrEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.AttrGroupEntity;
import com.yuan.mall.product.VO.AttrGroupWIthAttrsVo;
import com.yuan.mall.product.VO.SpuItemAttrGroup;
import com.yuan.mall.product.entity.AttrGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * 属性分组
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
public interface AttrGroupService extends IService<AttrGroupEntity> {

    PageUtils queryPage(Map<String, Object> params);

    PageUtils queryPage(Map<String, Object> params, Integer id);

    List<AttrGroupWIthAttrsVo> getAttrGroupWithAttrsByCategoryId(Long catelogId);

    List<SpuItemAttrGroup> getAttrGroupWithAttrsBySpuId(Long spuId, Long catelogId);
}

