package com.yuan.mall.auth.feign;

import com.yuan.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author Yuan Diao
 * @date 2022/3/1
 */

@FeignClient("gulimall-third-party")
public interface thirdFeignService {

    @GetMapping("/sms/sendcode")
    R sendCode(@RequestParam("phone") String phoneNumber, @RequestParam("code") String code);
}
