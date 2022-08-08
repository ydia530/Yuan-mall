package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.AttrGroupRelationVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yuan.mall.product.VO.AttrGroupRelationVo;
import com.yuan.mall.product.entity.AttrAttrgroupRelationEntity;

import java.util.Map;

/**
 * 属性&属性分组关联
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
public interface AttrAttrgroupRelationService extends IService<AttrAttrgroupRelationEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void deleteRelation(AttrGroupRelationVo[] vos);
}

