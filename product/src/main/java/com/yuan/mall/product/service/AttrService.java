package com.yuan.mall.product.service;

import com.yuan.mall.product.VO.AttrResponseVo;
import com.yuan.mall.product.VO.AttrVo;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yuan.common.utils.PageUtils;
import com.yuan.mall.product.entity.AttrEntity;
import com.yuan.mall.product.VO.AttrResponseVo;
import com.yuan.mall.product.VO.AttrVo;
import com.yuan.mall.product.entity.AttrEntity;

import java.util.List;
import java.util.Map;

/**
 * 商品属性
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
public interface AttrService extends IService<AttrEntity> {

    PageUtils queryPage(Long id, Map<String, Object> params, String attrType);

    void saveAttr(AttrVo attr);

    AttrResponseVo getInfoById(Long attrId);

    void updateInfo(AttrVo attr);

    List<AttrEntity> getRelationAttr(Long attrgroupId);

    void deleteAttr(List<Long> asList);

    PageUtils getNonRelationAttr(Long attrgroupId, Map<String, Object> params);

    List<Long> selectSearchAttrIds(List<Long> attrIds);
}

