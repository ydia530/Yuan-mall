package com.yuan.mall.ware;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author diaoyuan
 */

@EnableFeignClients(basePackages = "com.yuan.mall.ware.feign")
@MapperScan("com.yuan.mall.ware.dao")
@EnableDiscoveryClient
@SpringBootApplication
public class WareApplication {
    public static void main(String[] args) {
        SpringApplication.run(WareApplication.class, args);
    }

}
