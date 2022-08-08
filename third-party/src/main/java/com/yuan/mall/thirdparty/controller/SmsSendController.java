package com.yuan.mall.thirdparty.controller;

import com.yuan.common.utils.R;
import com.yuan.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author Yuan Diao
 * @date 2022/3/1
 */

@RestController
@RequestMapping("/sms")
public class SmsSendController {

    @Autowired
    SmsComponent smsComponent;

    /**
     * 为其他服务提供发送验证码请求；
     * @param phoneNumber
     * @return
     */
    @GetMapping("/sendcode")
    public R sendCode(@RequestParam("phone") String phoneNumber,
                      @RequestParam("code") String code){
        smsComponent.sendCode(code,phoneNumber);
        return R.ok();
    }

}
