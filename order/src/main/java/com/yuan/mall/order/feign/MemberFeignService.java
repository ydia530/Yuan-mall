package com.yuan.mall.order.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>Title: MemberFeignService</p>
 * Description：
 * date：2020/6/30 16:54
 */
@FeignClient("member")
public interface MemberFeignService {

	@GetMapping("member/address/list")
	R getAddress();

	@GetMapping("member/address/detail")
	R getAddressInfo(@RequestParam("id") Integer id);
}
