package com.yuan.mall.member.controller;

import com.yuan.common.utils.R;
import com.yuan.mall.member.annotation.LoginUser;
import com.yuan.mall.member.entity.UmsAddress;
import com.yuan.mall.member.services.UmsAddressService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/6
 */
@RestController
@RequestMapping("member/address")
public class UmsAddressController {
    @Resource
    private UmsAddressService addressService;

    @GetMapping("/list")
    public R getAddressList(@LoginUser Integer userId){
        if(userId == null) {
            return R.unLogin();
        }
        List<UmsAddress> addressList = addressService.getAddressList(userId);
        return R.ok().put("data",addressList);
    }

    @GetMapping("/detail")
    public R detail(@LoginUser Integer userId, @RequestParam Integer id){
        if(userId == null) {
            return R.unLogin();
        }
        UmsAddress address = addressService.getAddressDetail(userId, id);
        return R.ok().put("data", address);
    }


    @PostMapping("/save")
    public Object save(@LoginUser Integer userId, @RequestBody UmsAddress address){
        if(userId == null) {
            return R.unLogin();
        }
        return addressService.saveAddress(userId, address);
    }

    @GetMapping("/delete")
    public Object delete(@LoginUser Integer userId, @RequestParam Integer id){
        if(userId == null) {
            return R.unLogin();
        }
        return addressService.deleteAddress(userId, id);
    }



}
