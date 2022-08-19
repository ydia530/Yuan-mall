package com.yuan.mall.member.controller;

import com.yuan.common.to.UserInfoDto;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;
import com.yuan.common.vo.MemberRespVo;
import com.yuan.mall.member.annotation.LoginUser;
import com.yuan.mall.member.entity.UmsMember;
import com.yuan.mall.member.feign.CouponFeignService;
import com.yuan.mall.member.services.UmsMemberService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.regex.Pattern;

/**
 * 会员
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 */
@RestController
@RequestMapping("/member")
public class UmsMemberController {
    @Autowired
    private UmsMemberService memberService;

    @Autowired
    private CouponFeignService couponFeignService;

//    @RequestMapping("/coupons")
//    public R test(){
//        MemberEntity memberEntity = new MemberEntity();
//        memberEntity.setNickname("会员昵称张三");
//        R membercoupons = couponFeignService.membercoupons();
//        //打印会员和优惠券信息
//        return R.ok().put("member",memberEntity).put("coupons",membercoupons.get("coupons"));
//    }

    @PostMapping("login")
    public R login(@RequestBody UserInfoDto userInfoDto){
        MemberRespVo memberRespVo = memberService.login(userInfoDto);
        return R.ok().put("data", memberRespVo);
    }

    @GetMapping("info")
    public R login(@LoginUser Integer userId){
        if (userId == null){
            return R.error();
        }
        UmsMember user = memberService.getById(userId);

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("nickName", user.getNickname());
        data.put("avatar", user.getAvatar());
        data.put("gender", user.getGender());
        data.put("mobile", user.getMobile());

        return R.ok().put("data", data);
    }

    @GetMapping("change_nickname")
    public Object changeNickname(@LoginUser Integer userId, @RequestParam String nickname){
        if (userId == null) {
            return R.error();
        }
        if (StringUtils.isBlank(nickname) || nickname.length() > 15){
            return R.error();
        }
        Boolean res = memberService.updateNickname(userId, nickname);
        if (res){
            return R.ok();
        }
        return R.error();
    }



    @GetMapping("change_gender")
    public Object changeGender(@LoginUser Integer userId, @RequestParam Integer gender){
        if (userId == null) {
            return R.error();
        }
        if (gender == null){
            return R.error();
        }
        Boolean res = memberService.changeGender(userId, gender);
        if (res){
            return R.ok();
        }
        return R.error();
    }

    @GetMapping("change_mobile")
    public Object changeMobile(@LoginUser Integer userId, @RequestParam String mobile){
        if (userId == null) {
            return R.error();
        }
        if (!matchPhoneNumber(mobile)){
            return R.error();
        }

        Boolean res = memberService.changeMobile(userId, mobile);
        if (res){
            return R.ok();
        }
        return R.error();
    }


    /**
     * 验证手机号 由于号码段不断的更新，只需要判断手机号有11位，并且全是数字以及1开头
     * @param phoneNumber 手机号码
     * @return
     */
    private static boolean matchPhoneNumber(String phoneNumber) {
        String regex = "^1\\d{10}$";
        if(phoneNumber==null||phoneNumber.length()<=0){
            return false;
        }
        return Pattern.matches(regex, phoneNumber);
    }



    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
        UmsMember member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    public R save(@RequestBody UmsMember member){
		memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody UmsMember member){
		memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }


    @GetMapping("/search_history/add")
    public R addSearchHistory(@LoginUser Integer userId,  @RequestParam String search){
        if (userId != null){
            memberService.addSearchHistory(userId, search);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/search_history/delete")
    public R deleteSearchHistory(@LoginUser Integer userId){
        if (userId != null){
            memberService.deleteSearchHistory(userId);
            return R.ok();
        }
        return R.unLogin();
    }

    @GetMapping("/search_history")
    public R getSearchHistory(@LoginUser Integer userId){
        if (userId != null){
            Set<String> history = memberService.getSearchHistory(userId);
            return R.ok().put("data", history);
        }
        return R.unLogin();
    }

}
