package com.keda.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.keda.gulimall.search.common.ProductEsModel;
import com.keda.gulimall.search.config.ElasticsearchConfig;
import com.keda.gulimall.search.service.MallSearchService;
import com.keda.gulimall.search.vo.SearchParam;
import com.keda.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

/**
 * @author Jmd
 * @create 2024-05-2024/5/23-10:42
 * @Description：
 */

@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Resource
    private RestHighLevelClient esClient;

    @Override
    public SearchResult search(SearchParam searchParam){
        /*
        * 1. 当多个查询条件组合时，需要使用布尔查询进行组合
        * 2. 布尔查询中的must部分必须满足，should部分在没有must子句或filter子句的情况下，至少满足一个，存在上两者时，可以设置满足的数量
        * 3. 如果有嵌套查询nested, 也可以在嵌套查询中使用bool查询来同时设置多个条件，nested查询中，使用must子句来组合查询时，代表nested
        * 中数据的需要存在子数据同时满足组合的条件，如果不满足，子数据对应的上一级数据就查询不到，可以使用多个nested查询来匹配不同的nested
        * 属性或者匹配同一nested属性中不同条件的数据。
        * */
        String keyword = searchParam.getKeyword();
        List<Long> brandIdList = searchParam.getBrandId();
        Long catalog3Id = searchParam.getCatalog3Id();
        String price = searchParam.getPrice();
        String sort = searchParam.getSort();
        Integer hasStock = searchParam.getHasStock();
        List<String> attrs = searchParam.getAttrs();

        SearchRequest searchRequest = new SearchRequest(ElasticsearchConfig.PRODUCT_INDEX);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 设置keyword的查询，能够匹配categoryName 或 brandName
        if (!StringUtils.isEmpty(keyword)) {
            // 使用boolQueryBuilder查询，should的条件中满足一个即可
            /*BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
            boolQueryBuilder.should(QueryBuilders.matchQuery("categoryName", keyword));
            boolQueryBuilder.should(QueryBuilders.matchQuery("brandName", keyword));*/

            // 分词匹配
            MatchQueryBuilder skuTitleQueryBuilder = QueryBuilders.matchQuery("skuTitle", keyword);
            boolQueryBuilder.must(skuTitleQueryBuilder);
        }

        // 设置分类id的查询，必须匹配
        if (catalog3Id != null && catalog3Id != 0) {
            // termQuery，需要精确匹配上这个数据
            TermQueryBuilder categoryIdTermQueryBuilder = QueryBuilders.termQuery("categoryId", catalog3Id);
            boolQueryBuilder.must(categoryIdTermQueryBuilder); // 如果这样一个一个设置，下一个会被上一个覆盖
        }

        // 设置品牌列表的查询，必须匹配
        if (!CollectionUtils.isEmpty(brandIdList)){
            // termsQuery，需要精确匹配上列表中的一个数据
            TermsQueryBuilder brandIdTermsQueryBuilder = QueryBuilders.termsQuery("brandId", brandIdList);
            boolQueryBuilder.must(brandIdTermsQueryBuilder);
        }

        // 设置价格范围
        if (!StringUtils.isEmpty(price)) {
            // rangeQuery，按范围进行匹配
            RangeQueryBuilder skuPriceQueryBuiler = QueryBuilders.rangeQuery("skuPrice");
            String[] prices = price.split("_");
            if (price.startsWith("_")){
                skuPriceQueryBuiler.lte(prices[0]);
            }else if (price.endsWith("_")){
                skuPriceQueryBuiler.gte(prices[0]);
            }else {
                skuPriceQueryBuiler.gte(prices[0]).lte(prices[1]);
            }

            boolQueryBuilder.must(skuPriceQueryBuiler);
        }

        // 查询子属性
        if (!CollectionUtils.isEmpty(attrs)){
            attrs.forEach(attr->{
                String[] attrInfo = attr.split("_");
                // 首先构建一个布尔查询，要求nested数据中存在同时满足attrId和对应attrValue同时满足的数据
                BoolQueryBuilder boolQueryBuilderForNested = QueryBuilders.boolQuery();
                TermQueryBuilder termQueryBuilderForId = QueryBuilders.termQuery("attrs.attrId", attrInfo[0]);
                TermQueryBuilder termQueryBuilderForValue = QueryBuilders.termQuery("attrs.attrValue", attrInfo[1]);
                boolQueryBuilderForNested.must(termQueryBuilderForId);
                boolQueryBuilderForNested.must(termQueryBuilderForValue);
                // nestedQuery
                // 查询嵌套数据中满足条件的数据，如果不存在同时满足条件的嵌套数据，嵌套数据对应的数据查询不到
                // 可以使用多个此查询，用bool查询组合，以满足查询不同的嵌套数据会匹配同嵌套数据中的不同对象
                NestedQueryBuilder attrNestedQueryBuilder = QueryBuilders.nestedQuery("attrs",boolQueryBuilderForNested, ScoreMode.Avg);
                // 再由上边的布尔查询添加这个nested查询的子条件
                boolQueryBuilder.must(attrNestedQueryBuilder);

            });
        }

        if (hasStock != null) {

            Boolean isStockExist = null;
            if (hasStock == 0) {
                isStockExist = false;
            }else if (hasStock == 1) {
                isStockExist = true;
            }

            TermQueryBuilder hasStockTermBuilder = QueryBuilders.termQuery("hasStock", isStockExist);

            sourceBuilder.query(hasStockTermBuilder);
        }

        sourceBuilder.query(boolQueryBuilder);



        // 根据排序条件进行排序
        if (!StringUtils.isEmpty(sort)) {

            String[] sortConditions = sort.split("_");
            String sortName = sortConditions[0];
            SortOrder sortOrder = "asc".equalsIgnoreCase(sortConditions[1])?SortOrder.ASC:SortOrder.DESC;
            String sortBy = "";
            if ("saleCount".equals(sortName)){
                sortBy = "saleCount";
            }else if ("price".equals(sortName)){
                sortBy = "skuPrice";
            }else if ("hotScore".equals(sortName)){
                sortBy = "hotScore";
            }

            sourceBuilder.sort(sortBy,sortOrder);
        }

        // 分页设置
        sourceBuilder.from((searchParam.getPageNum()-1)*searchParam.getPageSize());
        sourceBuilder.size(searchParam.getPageSize());

        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = null;
        try {
            searchResponse = esClient.search(searchRequest, ElasticsearchConfig.COMMON_OPTIONS);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        SearchHit[] hits = searchResponse.getHits().getHits();
        ArrayList<ProductEsModel> productEsModelList = new ArrayList<>();
        Arrays.stream(hits).forEach(hit ->{
            ProductEsModel productEsModel = JSON.parseObject(hit.getSourceAsString(), ProductEsModel.class);
            productEsModelList.add(productEsModel);
        });

        return new SearchResult();
    }
}
