package com.yuan.mall.member.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.utils.R;
import com.yuan.common.utils.RegexUtil;
import com.yuan.mall.member.entity.UmsAddress;
import com.yuan.mall.member.services.UmsAddressService;
import com.yuan.mall.member.mapper.UmsAddressMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author diaoyuan
* @description 针对表【ums_address(收货地址表)】的数据库操作Service实现
* @createDate 2022-08-06 10:49:30
*/
@Service
public class UmsAddressServiceImpl extends ServiceImpl<UmsAddressMapper, UmsAddress>
    implements UmsAddressService{


    @Override
    public List<UmsAddress> getAddressList(Integer userId) {
        QueryWrapper<UmsAddress> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId);
        return this.baseMapper.selectList(addressQueryWrapper);
    }

    @Override
    public UmsAddress getAddressDetail(Integer userId, Integer id) {
        QueryWrapper<UmsAddress> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper.eq("user_id", userId);
        addressQueryWrapper.eq("id", id);
        return this.baseMapper.selectOne(addressQueryWrapper);
    }

    @Override
    public Object saveAddress(Integer userId, UmsAddress address) {
        Object error = validate(address);
        if (error != null) {
            return error;
        }
        address.setUserId(userId);
        if(address.getIsDefault() == 1){
            QueryWrapper<UmsAddress> addressQueryWrapper = new QueryWrapper<>();
            addressQueryWrapper.eq("user_id", userId);
            addressQueryWrapper.eq("is_default", 1);
            UmsAddress address1 = this.baseMapper.selectOne(addressQueryWrapper);
            if (address1 != null){
                address1.setIsDefault(0);
                this.updateById(address1);
            }
        }
        boolean res = this.saveOrUpdate(address);
        if (res) {
            return R.ok();
        }
        return R.updatedDataFailed();
    }

    @Override
    public Object deleteAddress(Integer userId, Integer id) {
        QueryWrapper<UmsAddress> addressQueryWrapper = new QueryWrapper<>();
        addressQueryWrapper
                .eq("user_id", userId)
                .eq("id", id);
        UmsAddress address = this.baseMapper.selectOne(addressQueryWrapper);
        if (address != null) {
            this.baseMapper.deleteById(address);
            return R.ok();
        }
        return R.badArgumentValue();
    }


    private Object validate(UmsAddress address) {
        String name = address.getName();
        String phone = address.getPhone();
        String provinceName = address.getProvinceName();
        String provinceCode = address.getProvinceCode();
        String cityCode = address.getCityCode();
        String cityName = address.getCityName();
        String districtCode = address.getDistrictCode();
        String districtName = address.getDistrictName();
        String detailedAddress = address.getDetailAddress();
        if (StringUtils.isAnyBlank(name, phone, provinceName, provinceCode, cityCode, cityName,
                detailedAddress, districtCode, districtName) || !RegexUtil.isMobileSimple(phone) ){
            return R.badArgument();
        }
        return null;
    }

}




