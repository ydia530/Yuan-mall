package com.yuan.mall.product.dao;

import com.yuan.mall.product.VO.SpuItemAttrGroup;
import com.yuan.mall.product.entity.AttrGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yuan.mall.product.VO.SpuItemAttrGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 属性分组
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:58
 */
@Mapper
public interface AttrGroupDao extends BaseMapper<AttrGroupEntity> {

    List<SpuItemAttrGroup> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}
