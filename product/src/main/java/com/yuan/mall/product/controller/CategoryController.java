package com.yuan.mall.product.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.yuan.mall.product.entity.CategoryEntity;
import com.yuan.mall.product.service.CategoryService;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.yuan.mall.product.entity.CategoryEntity;
import com.yuan.mall.product.service.CategoryService;
import com.yuan.common.utils.PageUtils;
import com.yuan.common.utils.R;



/**
 * 商品三级分类
 *
 * @author DY
 * @email ydia530@aucklanduni.ac.nz
 * @date 2022-01-08 13:42:57
 */
@RestController
@RequestMapping("/product/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;



    /**
     * 查出所有分类及子分类，组装成树
     */
    @RequestMapping("/list/tree")
    public R list(@RequestParam Map<String, Object> params){
        List<CategoryEntity> categoryEntityList = categoryService.listWithTree();
        return R.ok().put("data", categoryEntityList);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{catId}")
    public R info(@PathVariable("catId") Long catId){
		CategoryEntity category = categoryService.getById(catId);

        return R.ok().put("data", category);
    }

    /**
     * 拖拽排序
     */
    @PutMapping("/update/sort")
    public R updateSort(@RequestBody CategoryEntity[] categoryEntities){
        categoryService.updateBatchById(Arrays.asList(categoryEntities));
        return R.ok();
    }

    /**
     * 保存
     */
    @PostMapping("/save")
    public R save(@RequestBody CategoryEntity category){
		categoryService.save(category);

        return R.ok();
    }

    /**
     * 修改
     */
    @PutMapping("/update")
    public R update(@RequestBody CategoryEntity category){
		categoryService.updateCascade(category);
        return R.ok();
    }

    /**
     * 删除
     */
    @PostMapping("/delete")
    public R delete(@RequestBody Long[] catIds){
        //批量删除及其引用

        categoryService.removeMenusByIds(Arrays.asList(catIds));
        return R.ok();
    }

}
