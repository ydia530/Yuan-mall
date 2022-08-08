package com.yuan.mall.thirdparty;

import com.yuan.mall.thirdparty.component.SmsComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class GulimallThirdPartyApplicationTests {

    @Autowired
    SmsComponent smsComponent;

//    @Test
//    void smsTest() {
//        smsComponent.sendCode("522345","15726593197");
//    }
//
//    @Test
//    void contextLoads() {
//        String host = "https://gyytz.market.alicloudapi.com";
//        String path = "/sms/smsSend";
//        String method = "POST";
//        String appcode = "b4ab774a24a14de098cf3cd764bea7e1";
//        Map<String, String> headers = new HashMap<String, String>();
//        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
//        headers.put("Authorization", "APPCODE " + appcode);
//        Map<String, String> querys = new HashMap<String, String>();
//        querys.put("mobile", "18615910916");
//        querys.put("param", "**code**:12345,**minute**:5");
//        querys.put("smsSignId", "2e65b1bb3d054466b82f0c9d125465e2");
//        querys.put("templateId", "908e94ccf08b4476ba6c876d13f084ad");
//        Map<String, String> bodys = new HashMap<String, String>();
//        try {
//            HttpResponse response = HttpUtils.doPost(host, path, method, headers, querys, bodys);
//            System.out.println(response.toString());
//            //获取response的body
//            //System.out.println(EntityUtils.toString(response.getEntity()));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

}
