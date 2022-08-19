package com.yuan.mall.cart.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author Yuan Diao
 * @date 2022/8/11
 */

@FeignClient("ware")
public interface WareFeignService {

    @GetMapping("/ware/waresku/info/{skuId}")
    R skuInfo(@PathVariable("skuId") Long skuId);
}
