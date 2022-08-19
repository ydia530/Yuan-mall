package com.yuan.mall.member.services;

import com.yuan.common.to.UserInfoDto;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.vo.MemberRespVo;
import com.yuan.mall.member.entity.UmsMember;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
* @author diaoyuan
* @description 针对表【ums_member(会员)】的数据库操作Service
* @createDate 2022-08-05 21:21:13
*/
public interface UmsMemberService extends IService<UmsMember> {

    MemberRespVo login(UserInfoDto userInfoDto);

    Boolean updateNickname(Integer userId, String nickname);

    Boolean changeGender(Integer userId, Integer gender);

    Boolean changeMobile(Integer userId, String mobile);

    PageUtils queryPage(Map<String, Object> params);

    Set<String> getSearchHistory(Integer userId);

    List<String> addSearchHistory(Integer userId, String search);

    void deleteSearchHistory(Integer userId);
}
