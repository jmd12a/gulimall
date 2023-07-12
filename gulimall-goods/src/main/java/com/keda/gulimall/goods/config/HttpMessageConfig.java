package com.keda.gulimall.goods.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;


 @Configuration
public class HttpMessageConfig implements WebMvcConfigurer {

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        /*
        * 创建一个Fastjson的http消息转换器，设置消息转换的格式为浏览器适配
        * 这里的目的是为了避免Mybatis自动生成的id太长，使用fastJson的http消息转换器将Long类型的转换为String类型的避免丢失精度
        * */
        FastJsonHttpMessageConverter fastJsonConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        fastJsonConfig.setSerializerFeatures(SerializerFeature.BrowserCompatible);
        fastJsonConverter.setFastJsonConfig(fastJsonConfig);
        converters.add(0, fastJsonConverter); // 这里第一个参数是配置消息转换器在列表中位置，如果在后面的话，会被同类型的消息转换器覆盖
    }
}
