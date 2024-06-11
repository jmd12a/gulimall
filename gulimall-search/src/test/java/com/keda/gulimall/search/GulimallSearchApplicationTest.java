package com.keda.gulimall.search;

import com.alibaba.fastjson.JSON;
import com.keda.gulimall.search.common.NewBank;
import com.keda.gulimall.search.common.User;
import com.keda.gulimall.search.config.ElasticsearchConfig;
import lombok.Data;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.ElasticsearchClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.Strings;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.elasticsearch.search.rescore.RescorerBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Jmd
 * @create 2024-04-2024/4/18-12:34
 * @Description：
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GulimallSearchApplicationTest {

    @Resource
    private RestHighLevelClient esClient;




    @Test
    public void testIndex1() throws IOException {


        User user = new User();
        user.setAge(18);
        user.setAddress("郑州管城回族区");
        user.setName("张三");


        IndexRequest request = new IndexRequest("users").source(JSON.toJSONString(user), XContentType.JSON).id("1");

        IndexResponse response = esClient.index(request, ElasticsearchConfig.COMMON_OPTIONS);

        System.out.println();
    }

    @Test
    public void testGet() throws IOException {


        GetRequest request = new GetRequest("users").id("1");
        request.fetchSourceContext(new FetchSourceContext(false, new String[]{"name","age"}, Strings.EMPTY_ARRAY));
        GetResponse response = esClient.get(request, ElasticsearchConfig.COMMON_OPTIONS);
        String source = response.getSourceAsString();
        User user = JSON.parseObject(source, User.class);

        System.out.println();
    }

    @Test
    public void testSearch() throws IOException {

        // 创建请求并指定索引
        SearchRequest request = new SearchRequest("newbank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        sourceBuilder.query(QueryBuilders.matchQuery("address","kings place"));
        sourceBuilder.aggregation(AggregationBuilders.terms("ageAgg").field("age"))
                .aggregation(AggregationBuilders.terms("genderAgg").field("gender")
                        .subAggregation(AggregationBuilders.avg("balanceAgg").field("balance"))
        );

        sourceBuilder.from(0);
        sourceBuilder.size(10);
        System.out.println(sourceBuilder.toString());

       //  ArrayList<NewBank> newBanks = new ArrayList<>();

        request.source(sourceBuilder);

        SearchResponse response = esClient.search(request, ElasticsearchConfig.COMMON_OPTIONS);

        SearchHit[] hits = response.getHits().getHits();
        for (SearchHit hit : hits) {
            // newBanks.add(JSON.parseObject(hit.getSourceAsString(), NewBank.class));
            System.out.println("account: " + JSON.parseObject(hit.getSourceAsString(),NewBank.class));
        }

        Terms ageAgg = response.getAggregations().get("ageAgg");
        for (Terms.Bucket bucket : ageAgg.getBuckets()) {
            System.out.println("age: "+bucket.getKeyAsNumber()+"; count"+bucket.getDocCount());
        }

        Terms genderAgg = response.getAggregations().get("genderAgg");


        for (Terms.Bucket bucket : genderAgg.getBuckets()) {
            Avg balanceAgg = bucket.getAggregations().get("balanceAgg");

            double balanceAggValue = balanceAgg.getValue();

            System.out.println("gender: "+bucket.getKeyAsString()+"; count: "+bucket.getDocCount()+"; balance: "+balanceAggValue);
        }


    }
}
