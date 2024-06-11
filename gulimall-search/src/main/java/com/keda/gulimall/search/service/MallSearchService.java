package com.keda.gulimall.search.service;

import com.keda.gulimall.search.vo.SearchParam;
import com.keda.gulimall.search.vo.SearchResult;

/**
 * @author Jmd
 * @create 2024-05-2024/5/23-10:42
 * @Description：
 */
public interface MallSearchService {

    SearchResult search(SearchParam searchParam);

}
