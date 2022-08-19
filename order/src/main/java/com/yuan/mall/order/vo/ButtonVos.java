package com.yuan.mall.order.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Yuan Diao
 * @date 2022/8/18
 */
@Data
@AllArgsConstructor
public class ButtonVos {
    private  Boolean primary;

    private Integer type;

    private String name;
}
