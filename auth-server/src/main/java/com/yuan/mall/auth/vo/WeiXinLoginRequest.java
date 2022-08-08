package com.yuan.mall.auth.vo;

import com.yuan.common.vo.UserInfoVo;
import lombok.Data;

import java.io.Serializable;

/**
 * @author Yuan Diao
 * @date 2022/7/30
 */
@Data
public class WeiXinLoginRequest implements Serializable {
    private static final long serialVersionUID = 3191241716373120793L;

    private String code;

    private UserInfoVo userInfo;
}
