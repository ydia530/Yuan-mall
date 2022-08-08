package com.yuan.mall.auth.vo;

/**
 * @author Yuan Diao
 * @date 2022/3/3
 */
import lombok.Data;

@Data
public class SocialUser {

    private String accessToken;

    private String remindIn;

    private int expiresIn;

    private String uid;

    private String isrealname;
}
