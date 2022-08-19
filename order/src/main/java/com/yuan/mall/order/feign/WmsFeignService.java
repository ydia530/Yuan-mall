package com.yuan.mall.order.feign;

import com.yuan.common.utils.R;
import com.yuan.mall.order.vo.SkuHasStockVo;
import com.yuan.mall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * <p>Title: WmsFeignService</p>
 * Description：
 * date：2020/7/1 11:58
 */
@FeignClient("ware")
public interface WmsFeignService {

	@PostMapping("/ware/waresku/hasstock")
	List<SkuHasStockVo> getSkuHasStock(@RequestBody List<Long> SkuIds);


	@GetMapping("/ware/wareinfo/fare")
	R getFare(@RequestParam("addrId") Long addrId);

	/**
	 * 锁定库存
	 */
	@PostMapping("/ware/waresku/lock/order")
	R orderLockStock(@RequestBody WareSkuLockVo vo);

}
