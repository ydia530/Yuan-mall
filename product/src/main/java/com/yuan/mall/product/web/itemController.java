package com.yuan.mall.product.web;

import com.yuan.mall.product.VO.SkuItemVo;
import com.yuan.mall.product.service.SkuInfoService;
import com.yuan.mall.product.VO.SkuItemVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.concurrent.ExecutionException;

/**
 * @author Yuan Diao
 * @date 2022/2/27
 */
@Controller
public class itemController {

    @Autowired
    SkuInfoService skuInfoService;
    @GetMapping("/{skuId}.html")
    public String item(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {
        SkuItemVo skuItemVo = skuInfoService.item(skuId);
        model.addAttribute("item", skuItemVo);
        return "item";
    }
}
