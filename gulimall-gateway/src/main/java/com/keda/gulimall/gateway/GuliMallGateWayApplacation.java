package com.keda.gulimall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GuliMallGateWayApplacation {

    public static void main(String[] args) {
        SpringApplication.run(GuliMallGateWayApplacation.class, args);
    }
}
