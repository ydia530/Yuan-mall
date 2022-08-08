package com.yuan.mall.auth.feign;

import com.yuan.common.utils.R;
import com.yuan.mall.auth.vo.GithubUserVo;
import com.yuan.common.to.UserInfoDto;
import com.yuan.mall.auth.vo.UserLoginVo;
import com.yuan.mall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author Yuan Diao
 * @date 2022/3/1
 */
@FeignClient("member")
public interface MemberFeignService {

    @PostMapping("/member/register")
    R register(UserRegisterVo userRegisterVo);

    @PostMapping("/memberlogin")
    R login(UserLoginVo userLoginVo);

    @PostMapping("/member/login")
    R login(UserInfoDto userInfoDto);

    @PostMapping("/member/member/oauth2/login")
    R login(GithubUserVo githubUserVo);
}

