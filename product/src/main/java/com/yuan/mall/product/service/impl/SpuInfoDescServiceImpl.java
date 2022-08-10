package com.yuan.mall.product.service.impl;

import com.yuan.mall.product.dao.SpuInfoDescDao;
import com.yuan.mall.product.entity.SpuInfoDescEntity;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.product.dao.SpuInfoDescDao;
import com.yuan.mall.product.entity.SpuInfoDescEntity;
import com.yuan.mall.product.service.SpuInfoDescService;


@Service("spuInfoDescService")
public class SpuInfoDescServiceImpl extends ServiceImpl<SpuInfoDescDao, SpuInfoDescEntity> implements SpuInfoDescService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuInfoDescEntity> page = this.page(
                new Query<SpuInfoDescEntity>().getPage(params),
                new QueryWrapper<SpuInfoDescEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveSpuInfoDescription(SpuInfoDescEntity spuInfoDescEntity) {
        baseMapper.insert(spuInfoDescEntity);
    }

    @Override
    public String getDescBySpuId(Long spuId) {
        SpuInfoDescEntity entity = baseMapper.selectOne(new QueryWrapper<SpuInfoDescEntity>().eq("spu_id", spuId));
        if (entity != null) {
            return entity.getDecript();
        }
        return null;
    }

}
