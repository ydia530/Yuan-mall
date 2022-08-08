package com.yuan.mall.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;

/**
 * 会员
 * @TableName ums_member
 */
@TableName(value ="ums_member")
@Data
public class UmsMember implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 用户头像图片
     */
    private String avatar;

    /**
     * 性别：0 未知， 1男， 1 女
     */
    private Integer gender;

    /**
     * 生日
     */
    private Date birthday;

    /**
     * 最近一次登录时间
     */
    private LocalDateTime lastLoginTime;

    /**
     * 最近一次登录IP地址
     */
    private String lastLoginIp;

    /**
     * 0 普通用户，1 VIP用户，2 高级VIP用户
     */
    private Integer userLevel;

    /**
     * 微信登录openid
     */
    private String weixinOpenid;

    /**
     * 微信登录会话KEY
     */
    private String sessionKey;

    /**
     * 0 可用, 1 禁用, 2 注销
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
