package com.yuan.common.to;

import com.yuan.common.vo.UserInfoVo;
import lombok.Data;

/**
 * @author Yuan Diao
 * @date 2022/7/30
 */
@Data
public class UserInfoDto {
    private UserInfoVo userInfoVo;
    private String openId;
    private String sessionKey;
    private String ip;
}
