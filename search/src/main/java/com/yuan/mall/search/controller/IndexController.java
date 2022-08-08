package com.yuan.mall.search.controller;

import com.yuan.mall.search.service.SearchService;
import com.yuan.mall.search.vo.SearchParam;

import com.yuan.mall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>Title: IndexController</p>
 * Description：
 * date：2020/6/9 14:01
 */
@Controller
public class IndexController {

    @Autowired
    SearchService searchService;


    @GetMapping("/list.html")
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request){

        // 获取路径原生的查询属性
        searchParam.set_queryString(request.getQueryString());
        System.out.println(request.getQueryString());
        System.out.println(searchParam);
        // ES中检索到的结果 传递给页面
        SearchResult result = searchService.search(searchParam);
        model.addAttribute("result", result);
        return "list";
    }

}
