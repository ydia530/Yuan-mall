package com.yuan.mall.product.service.impl;

import com.yuan.mall.product.VO.AttrGroupRelationVo;
import com.yuan.mall.product.VO.AttrGroupRelationVo;
import com.yuan.mall.product.dao.AttrAttrgroupRelationDao;
import com.yuan.mall.product.entity.AttrAttrgroupRelationEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.product.dao.AttrAttrgroupRelationDao;
import com.yuan.mall.product.entity.AttrAttrgroupRelationEntity;
import com.yuan.mall.product.service.AttrAttrgroupRelationService;


@Service("attrAttrgroupRelationService")
public class AttrAttrgroupRelationServiceImpl extends ServiceImpl<AttrAttrgroupRelationDao, AttrAttrgroupRelationEntity> implements AttrAttrgroupRelationService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrAttrgroupRelationEntity> page = this.page(
                new Query<AttrAttrgroupRelationEntity>().getPage(params),
                new QueryWrapper<AttrAttrgroupRelationEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        attrAttrgroupRelationDao.deleteRelation(vos);
    }

}
