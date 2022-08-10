package com.yuan.mall.product.service.impl;

import com.yuan.mall.product.dao.SpuImagesDao;
import com.yuan.mall.product.entity.SpuImagesEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.product.dao.SpuImagesDao;
import com.yuan.mall.product.entity.SpuImagesEntity;
import com.yuan.mall.product.service.SpuImagesService;
import org.springframework.util.CollectionUtils;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<SpuImagesEntity> page = this.page(
                new Query<SpuImagesEntity>().getPage(params),
                new QueryWrapper<SpuImagesEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveImages(Long id, List<String> images) {
        if (CollectionUtils.isEmpty(images)){

        } else {
            List<SpuImagesEntity> collect = images.stream().map(img -> {
                SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
                spuImagesEntity.setSpuId(id);
                spuImagesEntity.setImgUrl(img);
                return spuImagesEntity;
            }).collect(Collectors.toList());
            this.saveBatch(collect);
        }
    }

    @Override
    public List<String> getImagesBySpuId(Long id) {
        if (id != null){
            QueryWrapper<SpuImagesEntity> spuImagesEntityQueryWrapper = new QueryWrapper<>();
            spuImagesEntityQueryWrapper.eq("spu_id", id);
            List<SpuImagesEntity> spuImagesEntities = this.baseMapper.selectList(spuImagesEntityQueryWrapper);
            List<String> images = spuImagesEntities.stream()
                    .map(spuImagesEntity -> spuImagesEntity.getImgUrl()).collect(Collectors.toList());
            return images;
        }
        return null;
    }


}
