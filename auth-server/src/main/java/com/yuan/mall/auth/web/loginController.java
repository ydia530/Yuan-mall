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
            log.info("\n?????? [" + userInfo.getNickName() + "] ??????");
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
        try {// ???????????????????????????
            return thirdFeignService.sendCode(phoneNumber, code);
        } catch (Exception e) {
            log.warn("??????????????????????????? [????????????]");
        }
        return R.ok();
    }
//
//    /**
//     * TODO ?????????????????????,??????session?????? ???????????????sessoin??? ?????????????????????
//     * <p>
//     * TODO 1. ???????????????session??????
//     * ??????
//     * RedirectAttributes redirectAttributes ??? ???????????????????????????
//     */
//    @PostMapping("/register")
//    public String register(@Valid UserRegisterVo userRegisterVo,
//                           BindingResult result,
//                           RedirectAttributes redirectAttributes) {
//        if (result.hasErrors()) {
//            // ??????????????????????????????????????????
//            Map<String, String> errors = result.getFieldErrors().stream().collect(
//                    Collectors.toMap(FieldError::getField, fieldError -> fieldError.getDefaultMessage()));
//            // addFlashAttribute ????????????????????????
//            redirectAttributes.addFlashAttribute("errors", errors);
//            return "redirect:http://auth.yuanmall.top/register.html";
//        }
//
//        // ???????????? ??????????????????
//        // 1.???????????????
//        String code = userRegisterVo.getCode();
//
//        String redis_code = stringRedisTemplate.opsForValue().get(AuthServerConstant.smsCodePrefix + userRegisterVo.getPhone());
//        if(!StringUtils.isEmpty(redis_code)){
//            // ???????????????
//            if(code.equals(redis_code.split("_")[0])){
//                // ???????????????
//                stringRedisTemplate.delete(AuthServerConstant.smsCodePrefix + userRegisterVo.getPhone());
//                // ??????????????????????????????
//                R r = memberFeignService.register(userRegisterVo);
//                if(r.getCode() == 0){
//                    // ????????????????????????
//                    return "redirect:http://auth.yuanmall.top/login.html";
//                }else{
//                    Map<String, String> errors = new HashMap<>();
//                    errors.put("msg",r.getData("msg",new TypeReference<String>(){}));
//                    // ????????????????????????
//                    redirectAttributes.addFlashAttribute("errors",errors);
//                    return "redirect:http://auth.yuanmall.top/register.html";
//                }
//            }else{
//                Map<String, String> errors = new HashMap<>();
//                errors.put("code", "???????????????");
//                // addFlashAttribute ????????????????????????
//                redirectAttributes.addFlashAttribute("errors", errors);
//                return "redirect:http://auth.yuanmall.top/register.html";
//            }
//        }else{
//            Map<String, String> errors = new HashMap<>();
//            errors.put("code", "???????????????");
//            // addFlashAttribute ????????????????????????
//            redirectAttributes.addFlashAttribute("errors", errors);
//            return "redirect:http://auth.yuanmall.top/register.html";
//        }
//    }
//
//    @PostMapping("/login") // auth
//    public String login(UserLoginVo userLoginVo, // from?????????????????????
//                        RedirectAttributes redirectAttributes,
//                        HttpSession session){
//        // ????????????
//        R r = memberFeignService.login(userLoginVo);
//        if(r.getCode() == 0){
//            // ????????????
//            MemberRespVo respVo = r.getData("data", new TypeReference<MemberRespVo>() {});
//            // ??????session
//            session.setAttribute(AuthServerConstant.LOGIN_USER, respVo);//loginUser
//            log.info("\n?????? [" + respVo.getUsername() + "] ??????");
//            return "redirect:http://yuanmall.top";
//        }else {
//            HashMap<String, String> error = new HashMap<>();
//            // ??????????????????
//            error.put("msg", r.getData("msg",new TypeReference<String>(){}));
//            redirectAttributes.addFlashAttribute("errors", error);
//            return "redirect:http://auth.yuanmall.top/login.html";
//        }
//    }
}
