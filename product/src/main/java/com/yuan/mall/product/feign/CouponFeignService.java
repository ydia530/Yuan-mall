package com.yuan.mall.product.feign;

import com.yuan.common.to.SkuReductionDto;
import com.yuan.common.to.SpuBoundsDto;
import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author Yuan Diao
 * @date 2022/1/21
 */

@FeignClient("coupon")
public interface CouponFeignService {

    @PostMapping("/coupon/spubounds/save")
    R saveSpuBounds(@RequestBody SpuBoundsDto spuBoundsDto);

    @PostMapping("/coupon/skufullreduction/saveInfo")
    R saveSkuReduction(@RequestBody SkuReductionDto skuReductionDto);
}
