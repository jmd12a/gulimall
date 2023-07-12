package com.keda.gulimall.gateway.config;

import io.netty.handler.codec.http.cors.CorsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.concurrent.TimeUnit;

//  @Configuration 允许跨域的实现方式一，使用springboot提供允许跨域的过滤器
public class GulimallCorsConfig {

    @Bean // 直接把这个过滤器注入到容器中，springboot会根据这个过滤器的配置进行跨域设置
    public CorsWebFilter getCorsConfig(){
        // 基于url配置允许跨域
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        // 跨域具体设置
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOrigin("*"); // 允许来自那些请求的网址跨域
        configuration.setAllowCredentials(true); // 允许跨域请求携带cookie
        configuration.setMaxAge((long) (36000)); // 这次跨域检测的有效时间

        source.registerCorsConfiguration("/**",configuration);
        return new CorsWebFilter(source);
    }
}

// 允许跨域的配置方式二 ,使用webmvc提供的WebMvcConfigurer配置类进行配置
/*
*
* public class GulimallCrConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowCredentials(true)
                .allowedMethods("*")
                .allowedHeaders("*")
                .maxAge(Long.valueOf(36000));
    }
}
*/