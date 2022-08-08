package com.yuan.mall.ware.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @author Yuan Diao
 * @date 2022/1/24
 */

@FeignClient("gateway")
public interface ProductFeignService {

    @GetMapping("/api/product/skuinfo/info/{skuId}")
    public R info(@PathVariable("skuId") Long skuId);



}
