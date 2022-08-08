package com.yuan.mall.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 收货地址表
 * @TableName ums_address
 */
@TableName(value ="ums_address")
@Data
public class UmsAddress implements Serializable {
    /**
     *
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 收货人名称
     */
    private String name;

    /**
     * 用户表的用户ID
     */
    private Integer userId;

    /**
     * 行政区域表的省ID
     */
    private String provinceCode;

    /**
     * 行政区域表的省名字
     */
    private String provinceName;

    /**
     * 国家ID
     */
    private String countryCode;

    /**
     * 国家名
     */
    private String countryName;

    /**
     * 行政区域表的市ID
     */
    private String cityCode;

    /**
     * 行政区域表的市ID
     */
    private String cityName;

    /**
     * 行政区域表的区ID
     */
    private String districtCode;

    /**
     * 行政区域表的区ID
     */
    private String districtName;

    /**
     * 详细收货地址
     */
    private String detailAddress;

    /**
     * 手机号码
     */
    private String phone;

    /**
     * 是否默认地址
     */
    private Integer isDefault;

    /**
     * 详细收货地址
     */
    private String addressTag;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 逻辑删除
     */
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
