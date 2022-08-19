package com.yuan.mall.order.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * <p>Title: ProductFeignService</p>
 * Description：
 * date：2020/7/2 0:43
 */
@FeignClient("product")
public interface ProductFeignService {

	@GetMapping("/product/spuinfo/skuId/{id}")
	R getSpuInfoBySkuId(@PathVariable("id") Long skuId);

	@GetMapping("/product/skuinfo/info/{skuId}")
	R getSkuInfo(@PathVariable("skuId") Long skuId);

}
