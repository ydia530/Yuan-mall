package com.yuan.mall.product.VO;

import lombok.Data;

import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/8
 */
@Data
public class SpecItemVo {

    public Long specId;

    public List<SpecValueVo> specValueList;

    public String title;


}
