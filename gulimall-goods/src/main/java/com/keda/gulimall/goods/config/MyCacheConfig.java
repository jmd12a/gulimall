package com.keda.gulimall.goods.config;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Jmd
 * @create 2024-05-2024/5/22-9:59
 * @Description：
 */

@Configuration
@EnableCaching
@EnableConfigurationProperties(CacheProperties.class)
public class MyCacheConfig {

    /*
    * 在这个方法中创建了新的RedisCacheConfiguration对象并返回到容器中，如果容器中存在这个对象，那么springCache就不会使用默认对象
    * 不使用默认对象，就不会加载在application.yml文件中的配置，由此我们需要自己写加载的代码
    *
    * */
    @Bean
    public RedisCacheConfiguration getRedisCacheConfiguration(CacheProperties cacheProperties) {

        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();

        CacheProperties.Redis redisProperties = cacheProperties.getRedis();

        // 这里需要接受一下 因为返回的是新的对象
        // RedisCacheConfiguration 是不可变对象，所以每次修改配置都会生成一个新的对象。因此，必须将方法调用的结果重新赋值给 config 变量。
        config = config.serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));
        config = config.serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));

        if (redisProperties.getTimeToLive() != null) {
            config = config.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            config = config.prefixCacheNameWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            config = config.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            config = config.disableKeyPrefix();
        }

        return config;
    }
}
