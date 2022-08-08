package com.yuan.mall.auth.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.yuan.common.constant.AuthServerConstant;
import com.yuan.common.utils.HttpUtils;
import com.yuan.common.utils.R;
import com.yuan.common.vo.MemberRespVo;
import com.yuan.mall.auth.feign.MemberFeignService;
import com.yuan.mall.auth.vo.GithubUserVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.yuan.common.constant.AuthServerConstant.GITHUB_CODE;

/**
 * @author Yuan Diao
 * @date 2022/3/3
 */
@Slf4j
@Controller
@RequestMapping("/oauth2.0")
public class Oath2Controller {

    @Autowired
    private MemberFeignService memberFeignService;
//
//    @GetMapping("/logout")
//    public String login(HttpSession session){
//        if(session.getAttribute(AuthServerConstant.LOGIN_USER) != null){
//            log.info("\n[" +
//                    ((MemberRespVo)session.getAttribute(AuthServerConstant.LOGIN_USER)).getUsername()
//                    + "] 已下线");
//        }
//        session.invalidate();
//        return "redirect:http://auth.yuanmall.top/login.html";
//    }
//
//    /**
//     * 登录成功回调
//     */
//    @GetMapping("/github/success") // Oath2Controller
//    public String github(@RequestParam("code") String code, @RequestParam("state") String state, HttpSession session, HttpServletResponse servletResponse) throws Exception {
//        if(!GITHUB_CODE.equals(state)) {
//            throw new Exception("State验证失败");
//        }
//        // 根据code换取 Access Token
//        Map<String,String> map = new HashMap<>();
//        map.put("client_id", "8f40f03663cc9b321062");
//        map.put("client_secret", "6db6dc9ee3005ffbfdede098586e35d3a1c707e8");
//        map.put("redirect_uri", "http://auth.yuanmall.top/oauth2.0/github/success");
//        map.put("code", code);
//        Map<String, String> headers = new HashMap<>();
//
//        // 去获取token
//        HttpResponse response = HttpUtils.doPost("https://github.com/login",
//                "/oauth/access_token", "post", headers, null, map);
//        if(response.getStatusLine().getStatusCode() == 200){
//            HttpEntity entity = response.getEntity();
//            if (null != entity) {
//                String responseContent = EntityUtils.toString(entity, "UTF-8" );
//                EntityUtils.consume(entity);
//                Map<String, String> responseMap = params2Map(responseContent);
//                // 如果返回的map中包含error，表示失败，错误原因存储在error_description
//                if(responseMap.containsKey("error")) {
//                    throw  new Exception(responseMap.get("error_description"));
//                }
//
//                // 如果返回结果中包含access_token，表示成功
//                if(!responseMap.containsKey("access_token")) {
//                    throw  new Exception("获取token失败");
//                }
//                //用令牌换取用户信息
//                // 得到token和token_type
//                String accessToken = responseMap.get("access_token");
//                String tokenType = responseMap.get("token_type");
//
//                Map<String,String> query = new HashMap<>();
//                query.put("access_token",accessToken);
//                query.put("token_type", tokenType);
//                Map<String,String> header = new HashMap<>();
//                header.put("Authorization", "token "+  accessToken);
//                HttpResponse userInfoResp = HttpUtils.doGet("https://api.github.com", "/user", null, header, query);
//                HttpEntity infoResponse = userInfoResp.getEntity();
//                String infoResponseContent = EntityUtils.toString(infoResponse, "UTF-8" );
//                EntityUtils.consume(infoResponse);
//                GithubUserVo githubUser = JSON.parseObject(infoResponseContent, GithubUserVo.class);
//                R login = memberFeignService.login(githubUser);
//                // 1.如果用户是第一次进来 自动注册进来(为当前社交用户生成一个会员信息 以后这个账户就会关联这个账号)
//                if(login.getCode() == 0){
//                    MemberRespVo respVo = login.getData("data" ,new TypeReference<MemberRespVo>() {});
//                    log.info("\n欢迎 [" + respVo.getUsername() + "] 使用社交账号登录");
//                    // 放入session
//                    session.setAttribute(AuthServerConstant.LOGIN_USER, respVo);//loginUser
//                    return "redirect:http://yuanmall.top";
//                }
//            }
//            return "redirect:http://auth.yuanmall.top/login.html";
//        }else{
//            return "redirect:http://auth.yuanmall.top/login.html";
//        }
//    }
//
//    public static Map<String, String> params2Map(String result) {
//        System.out.println(result);
//        Map<String,String> map=new HashMap<>();
//        map.put("access_token",result.substring(13,53));
//        map.put("token_type",result.substring(result.length()-6,result.length()));
//        map.put("result",result);
//        return map;
//    }
}
