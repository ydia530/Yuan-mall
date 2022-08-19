package com.yuan.mall.ware.service.impl;


import com.yuan.mall.ware.dao.WareInfoDao;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Random;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.Query;

import com.yuan.mall.ware.entity.WareInfoEntity;
import com.yuan.mall.ware.service.WareInfoService;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;


@Service("wareInfoService")
public class WareInfoServiceImpl extends ServiceImpl<WareInfoDao, WareInfoEntity> implements WareInfoService {



    @Override
    public PageUtils queryPage(Map<String, Object> params) { // 传入分页信息
        QueryWrapper<WareInfoEntity> wrapper = new QueryWrapper<>();

        // 查询关键字
        String key = (String) params.get("key");
        if (!StringUtils.isEmpty(key)) {
            // 仓库编号、仓库名字、仓库地址、区域编号
            wrapper.eq("id", key).or().like("name", key).or().like("address", key).or().like("areacode", key);
        }
        // 执行
        IPage<WareInfoEntity> page = this.page(
                new Query<WareInfoEntity>().getPage(params),
                wrapper
        );
        return new PageUtils(page);
    }


}
