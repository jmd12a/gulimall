package com.keda.gulimall.search.web;

import com.keda.gulimall.search.service.MallSearchService;
import com.keda.gulimall.search.vo.SearchParam;
import com.keda.gulimall.search.vo.SearchResult;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.ServletResponse;

/**
 * @author Jmd
 * @create 2024-05-2024/5/22-17:30
 * @Description：
 */


@Controller
public class IndexController {


    @Resource
    private MallSearchService searchService;

    @RequestMapping({"/index","index.html","list.html"})
    public String getIndex(SearchParam searchParam){

        SearchResult result = searchService.search(searchParam);
        return "index";
    }

}
