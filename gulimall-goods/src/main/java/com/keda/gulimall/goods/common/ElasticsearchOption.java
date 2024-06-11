package com.keda.gulimall.goods.common;

import org.elasticsearch.client.RequestOptions;

/**
 * @author Jmd
 * @create 2024-04-2024/4/18-15:00
 * @Description：
 */

public class ElasticsearchOption {

    public static final RequestOptions COMMON_OPTIONS;

    public static final String PRODUCT_INDEX = "product";

    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
//        builder.addHeader("Authorization", "Bearer " + TOKEN);
//        builder.setHttpAsyncResponseConsumerFactory(
//                new HttpAsyncResponseConsumerFactory
//                        .HeapBufferedResponseConsumerFactory(30 * 1024 * 1024 * 1024));
        COMMON_OPTIONS = builder.build();
    }
}
