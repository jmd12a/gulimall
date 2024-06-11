package com.keda.gulimall.goods.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jmd
 * @create 2024-04-2024/4/18-12:26
 * @Description：
 */

@Configuration
public class ElasticsearchConfig {

    @Bean
    public RestHighLevelClient getRestHighLevelClient() {

        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost("192.168.153.128", 9200, "http"));

        return new RestHighLevelClient(clientBuilder);
    }
}
