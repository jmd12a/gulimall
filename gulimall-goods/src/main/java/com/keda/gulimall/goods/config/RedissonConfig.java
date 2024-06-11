package com.keda.gulimall.goods.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jmd
 * @create 2024-05-2024/5/17-18:37
 * @Description：
 */

@Configuration
public class RedissonConfig {

    // destroyMethod 标注服务结束时销毁这个对象的方法，指向的是该类型RedissonClient中对应的方法，即RedissonClient.shutdown()
    @Bean(destroyMethod = "shutdown")
    public RedissonClient getRedissonClient() {

        Config config = new Config();
        // 地址之前要加上redis:// ，如果是安全模式连接的话要使用"rediss://
        config.useSingleServer().setAddress("redis://192.168.153.128:6379");

        return Redisson.create(config);
    }
}
