package com.yuan.mall.auth.web;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.alibaba.fastjson.TypeReference;
import com.yuan.common.constant.AuthServerConstant;
import com.yuan.common.exception.BizCodeEnum;
import com.yuan.common.utils.IpUtil;
import com.yuan.common.utils.R;
import com.yuan.common.vo.MemberRespVo;
import com.yuan.common.vo.UserInfoVo;
import com.yuan.mall.auth.feign.MemberFeignService;
import com.yuan.mall.auth.feign.thirdFeignService;
import com.yuan.common.to.UserInfoDto;
import com.yuan.mall.auth.vo.WeiXinLoginRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @author Yuan Diao
 * @date 2022/2/28
 */
@Slf4j
@RestController()
@RequestMapping("auth")
public class loginController {

    @Autowired
    private thirdFeignService thirdFeignService;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    MemberFeignService memberFeignService;


    @Autowired
    private WxMaService wxService;

    @PostMapping("login_by_weixin")
    public R loginInByWeiXin(@RequestBody WeiXinLoginRequest weiXinLoginRequest,
                                  HttpServletRequest request){
        String code = weiXinLoginRequest.getCode();
        UserInfoVo userInfo = weiXinLoginRequest.getUserInfo();
        if(StringUtils.isBlank(code) || userInfo == null){
            return R.error();
        }
        String sessionKey = null;
        String openId = null;
        try {
            WxMaJscode2SessionResult result = this.wxService.getUserService().getSessionInfo(code);
            sessionKey = result.getSessionKey();
            openId = result.getOpenid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(StringUtils.isAnyBlank(sessionKey, openId)){
            return R.error();
        }
        UserInfoDto dto = new UserInfoDto();
        dto.setSessionKey(sessionKey);
        dto.setOpenId(openId);
        dto.setUserInfoVo(userInfo);
        dto.setIp(IpUtil.getIpAddr(request));

        R r = memberFeignService.login(dto);
        if (r.getCode() == 0){
            MemberRespVo respVo = r.getData("data", new TypeReference<MemberRespVo>() {});
            log.info("\n欢迎 [" + userInfo.getNickName() + "] 登录");
            return r;
        }

        return null;
    }







    @ResponseBody
    @GetMapping("/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phoneNumber) {
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.smsCodePrefix + phoneNumber);
        if (!StringUtils.isEmpty(redisCode)) {
            Long createTime = Long.valueOf(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - createTime < 60000) {
                return R.error(BizCodeEnum.SMS_EXCEPTION.getCode(), BizCodeEnum.SMS_EXCEPTION.getMessage());
            }
        }

        String code = UUID.randomUUID().toString().substring(0, 5);
        stringRedisTemplate.opsForValue().set(AuthServerConstant.smsCodePrefix + phoneNumber,
                code + "_" + System.currentTimeMillis(), 5, TimeUnit.MINUTES);
        try {// 调用第三方短信服务
            return thirdFeignService.sendCode(phoneNumber, code);
        } catch (Exception e) {
            log.warn("远程调用不知名错误 [无需解决]");
        }
        return R.ok();
    }
//
//    /**
//     * TODO 重定向携带数据,利用session原理 将数据放在sessoin中 取一次之后删掉
//     * <p>
//     * TODO 1. 分布式下的session问题
//     * 校验
//     * RedirectAttributes redirectAttributes ： 模拟重定向带上数据
//     */
//    @PostMapping("/register")
//    public String register(@Valid UserRegisterVo userRegisterVo,
//                           BindingResult result,
//                           RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            // 将错误属性与错误信息一一封装
//            Map<String, String> errors = result.getFieldErrors().stream().collect(
//                    Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage()));
//            // addFlashAttribute 这个数据只取一次
//            redirectAttributes.addFlashAttribute("errors", errors);
//            return "redirect:http://auth.yuanmall.top/register.html";
//        }
//
//        // 开始注册 调用远程服务
//        // 1.校验验证码
//        String code = userRegisterVo.getCode();
//
//        String redis_code = stringRedisTemplate.opsForValue().get(AuthServerConstant.smsCodePrefix + userRegisterVo.getPhone());
//        if(!StringUtils.isEmpty(redis_code)){
//            // 验证码通过
//            if(code.equals(redis_code.split("_")[0])){
//                // 删除验证码
//                stringRedisTemplate.delete(AuthServerConstant.smsCodePrefix + userRegisterVo.getPhone());
//                // 调用远程服务进行注册
//                R r = memberFeignService.register(userRegisterVo);
//                if(r.getCode() == 0){
//                    // 注册成功，去登录
//                    return "redirect:http://auth.yuanmall.top/login.html";
//                }else{
//                    Map<String, String> errors = new HashMap<>();
//                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
//                    // 数据只需要取一次
//                    redirectAttributes.addFlashAttribute("errors",errors);
//                    return "redirect:http://auth.yuanmall.top/register.html";
//                }
//            }else{
//                Map<String, String> errors = new HashMap<>();
//                errors.put("code", "验证码错误");
//                // addFlashAttribute 这个数据只取一次
//                redirectAttributes.addFlashAttribute("errors", errors);
//                return "redirect:http://auth.yuanmall.top/register.html";
//            }
//        }else{
//            Map<String, String> errors = new HashMap<>();
//            errors.put("code", "验证码错误");
//            // addFlashAttribute 这个数据只取一次
//            redirectAttributes.addFlashAttribute("errors", errors);
//            return "redirect:http://auth.yuanmall.top/register.html";
//        }
//    }
//
//    @PostMapping("/login") // auth
//    public String login(UserLoginVo userLoginVo, // from表单里带过来的
//                        RedirectAttributes redirectAttributes,
//                        HttpSession session){
//        // 远程登录
//        R r = memberFeignService.login(userLoginVo);
//        if(r.getCode() == 0){
//            // 登录成功
//            MemberRespVo respVo = r.getData("data", new TypeReference<MemberRespVo>() {});
//            // 放入session
//            session.setAttribute(AuthServerConstant.LOGIN_USER, respVo);//loginUser
//            log.info("\n欢迎 [" + respVo.getUsername() + "] 登录");
//            return "redirect:http://yuanmall.top";
//        }else {
//            HashMap<String, String> error = new HashMap<>();
//            // 获取错误信息
//            error.put("msg", r.getData("msg",new TypeReference<String>(){}));
//            redirectAttributes.addFlashAttribute("errors", error);
//            return "redirect:http://auth.yuanmall.top/login.html";
//        }
//    }
}
