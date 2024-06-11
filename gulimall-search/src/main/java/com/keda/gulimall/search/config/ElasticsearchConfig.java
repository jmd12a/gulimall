package com.keda.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jmd
 * @create 2024-04-2024/4/18-12:26
 * @Description：
 */

@Configuration
public class ElasticsearchConfig {

    public static final String PRODUCT_INDEX = "product";

    public static final RequestOptions COMMON_OPTIONS;
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }

    @Bean
    public RestHighLevelClient getRestHighLevelClient() {

        RestClientBuilder clientBuilder = RestClient.builder(new HttpHost("192.168.153.128", 9200, "http"));

        return new RestHighLevelClient(clientBuilder);
    }
}
