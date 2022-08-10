package com.yuan.mall.product.VO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Yuan Diao
 * @date 2022/8/8
 */
@Data
public class SpuVo {
    /** 可购买数 **/
    public Long available;

    public List<Long> categoryIds;

    /** 商品描述图 **/
    public List<String> desc;

    public List<String> images;

    public Integer isPutOnSale;

    public List<String> limitInfo;

    public BigDecimal maxLinePrice;

    public BigDecimal maxSalePrice;

    public BigDecimal minLinePrice;

    public BigDecimal minSalePrice;

    public String primaryImage;

    public List<SkuVo> skuList;

    public Long soldNum;

    public List<SpecItemVo> specList;

    public Long spuId;

    public Long spuStockQuantity;

    public List<String> spuTagList;

    public String title;

}
