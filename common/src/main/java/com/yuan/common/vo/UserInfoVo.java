package com.yuan.common.vo;

import lombok.Data;

/**
 * @author Yuan Diao
 * @date 2022/8/5
 */
@Data
public class UserInfoVo {
    private String nickName;
    private String avatarUrl;
    private String country;
    private String province;
    private String city;
    private String language;
    private Integer gender;
}
