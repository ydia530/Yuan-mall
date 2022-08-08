package com.yuan.mall.member.services.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yuan.common.to.UserInfoDto;
import com.yuan.common.utils.UserTokenManager;
import com.yuan.common.vo.MemberRespVo;
import com.yuan.common.vo.UserInfoVo;
import com.yuan.mall.member.entity.UmsMember;
import com.yuan.mall.member.services.UmsMemberService;
import com.yuan.mall.member.mapper.UmsMemberMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
* @author diaoyuan
* @description 针对表【ums_member(会员)】的数据库操作Service实现
* @createDate 2022-08-05 21:21:13
*/
@Service
public class UmsMemberServiceImpl extends ServiceImpl<UmsMemberMapper, UmsMember>
    implements UmsMemberService{

    @Override
    public MemberRespVo login(UserInfoDto userInfoDto) {

        QueryWrapper<UmsMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("weixin_openid", userInfoDto.getOpenId());
        UserInfoVo userInfo = userInfoDto.getUserInfoVo();

        UmsMember user = this.baseMapper.selectOne(queryWrapper);
        if (user == null) {
            user = new UmsMember();
            user.setUsername(userInfoDto.getOpenId());
            user.setPassword(userInfoDto.getOpenId());
            user.setWeixinOpenid(userInfoDto.getOpenId());
            user.setAvatar(userInfo.getAvatarUrl());
            user.setNickname(userInfo.getNickName());
            user.setGender(userInfo.getGender());
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(userInfoDto.getIp());
            user.setSessionKey(userInfoDto.getSessionKey());
            this.save(user);
        } else {
            user.setLastLoginTime(LocalDateTime.now());
            user.setLastLoginIp(userInfoDto.getIp());
            user.setSessionKey(userInfoDto.getSessionKey());
            if (this.updateById(user) == false) {
                return null;
            }
        }
        // token
        String token = UserTokenManager.generateToken(user.getId());
        MemberRespVo memberRespVo = new MemberRespVo();
        memberRespVo.setToken(token);
        memberRespVo.setUserInfoVo(userInfo);
        return memberRespVo;
    }


    @Override
    public Boolean updateNickname(Integer userId, String nickname) {
        UmsMember user = new UmsMember();
        user.setId(userId);
        user.setNickname(nickname);
        return this.updateById(user);
    }

    @Override
    public Boolean changeGender(Integer userId, Integer gender) {
        UmsMember user = new UmsMember();
        user.setId(userId);
        user.setGender(gender);
        return this.updateById(user);
    }

    @Override
    public Boolean changeMobile(Integer userId, String mobile) {
        UmsMember user = new UmsMember();
        user.setId(userId);
        user.setMobile(mobile);
        return this.updateById(user);
    }
}




