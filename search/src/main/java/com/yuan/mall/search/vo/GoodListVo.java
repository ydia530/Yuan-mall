package com.yuan.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/7
 */
@Data
public class GoodListVo {
    public Integer pageNum;

    public Integer pageSize;

    public Long totalCount;

    public List<GoodVo> spuList;

}
