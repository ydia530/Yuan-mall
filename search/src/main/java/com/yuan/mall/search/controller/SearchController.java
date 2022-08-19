package com.yuan.mall.search.controller;

import com.yuan.common.utils.R;
import com.yuan.mall.search.service.SearchService;
import com.yuan.mall.search.vo.GoodListVo;
import com.yuan.mall.search.vo.SearchParam;
import com.yuan.mall.search.vo.SearchResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Set;

/**
 * @author Yuan Diao
 * @date 2022/8/7
 */
@RestController
@RequestMapping("/search")
public class SearchController {
    @Resource
    private SearchService searchService;

    @GetMapping("/list")
    public R searchList(SearchParam searchParam, HttpServletRequest request){
        GoodListVo result = searchService.searchVx(searchParam);
        return R.ok().put("data", result);
    }

    @GetMapping("hot_search")
    public R searchHistory(){
        Set<String> hot = searchService.getSearchHistory();
        return R.ok().put("data", hot);
    }

}
