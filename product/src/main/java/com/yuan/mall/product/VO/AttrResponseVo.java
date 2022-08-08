package com.yuan.mall.product.VO;


import lombok.Data;

@Data
public class AttrResponseVo extends AttrVo {


    /**
     * 所属分类名
     */
    private String catelogName;

    /**
     * 所属分组名
     */
    private String groupName;

    /**
     * 完整分类名
     */
    private Long[] catelogPath;

}
