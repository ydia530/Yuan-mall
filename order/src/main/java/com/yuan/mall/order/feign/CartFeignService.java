package com.yuan.mall.order.feign;

import com.yuan.common.utils.R;
import com.yuan.mall.order.config.FeignConfig;
import com.yuan.mall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * <p>Title: CartFeignService</p>
 * Description：
 * date：2020/6/30 18:08
 */
@FeignClient(name= "cart", configuration = {FeignConfig.class})
public interface CartFeignService {

	@GetMapping("/currentUserCartItems")
	List<OrderItemVo> getCurrentUserCartItems();

	@GetMapping("/cart/items_order")
	R getCartItemsForOrder();
}
