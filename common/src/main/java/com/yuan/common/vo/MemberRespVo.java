package com.yuan.common.vo;

/**
 * @author Yuan Diao
 * @date 2022/3/2
 */
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>Title: MemberRsepVo</p>
 * Description：
 * date：2020/6/26 17:17
 */
@ToString
@Data
public class MemberRespVo implements Serializable {
    private String token;
    private UserInfoVo userInfoVo;
}
