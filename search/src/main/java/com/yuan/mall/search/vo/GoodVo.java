package com.yuan.mall.search.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/7
 */
@Data
public class GoodVo {
    public Integer available;

    public Long categoryIds;

    public String title;

    public List<String> spuTagList;

    public BigDecimal price;

    public BigDecimal originPrice;

    public String thumb;

    private Long spuId;
}
