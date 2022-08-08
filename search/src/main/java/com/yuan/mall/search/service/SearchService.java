package com.yuan.mall.search.service;

import com.yuan.mall.search.vo.GoodListVo;
import com.yuan.mall.search.vo.SearchParam;
import com.yuan.mall.search.vo.SearchResult;

/**
 * @author Yuan Diao
 * @date 2022/2/21
 */
public interface SearchService {

    /**
     * @param searchParam 检索条件
     * @return 检索结果
     */
    SearchResult search(SearchParam searchParam);

    GoodListVo searchVx(SearchParam searchParam);
}
