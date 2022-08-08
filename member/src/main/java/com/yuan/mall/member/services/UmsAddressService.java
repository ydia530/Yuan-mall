package com.yuan.mall.member.services;

import com.yuan.mall.member.entity.UmsAddress;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
* @author diaoyuan
* @description 针对表【ums_address(收货地址表)】的数据库操作Service
* @createDate 2022-08-06 10:49:30
*/
public interface UmsAddressService extends IService<UmsAddress> {

    List<UmsAddress> getAddressList(Integer userId);

    UmsAddress getAddressDetail(Integer userId, Integer id);

    Object saveAddress(Integer userId, UmsAddress address);

    Object deleteAddress(Integer userId, Integer id);
}
