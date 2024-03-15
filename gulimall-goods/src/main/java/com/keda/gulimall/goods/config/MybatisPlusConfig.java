package com.keda.gulimall.goods.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.keda.gulimall.goods.dao")
public class MybatisPlusConfig {

    @Bean // 将这个bean的返回值返回给Spring的容器
    public MybatisPlusInterceptor mybatisPlusInterceptor(){
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setOverflow(true); // 如果请求页码过大，自动转到首页
        interceptor.addInnerInterceptor(paginationInnerInterceptor);

        return interceptor;
    }

    @Bean
    public MetaObjectHandler getMetaObjectHandler(){
        return new MybatisPlusAutoFillConfig();
    }
}
