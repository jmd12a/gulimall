package com.keda.gulimall.goods;

import com.keda.gulimall.fegin.feginClients.AttrAttrgroupRelationClients;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
@EnableTransactionManagement // 开启事务
public class GulimallGoodsApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGoodsApplication.class, args);
    }

}
