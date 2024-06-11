package com.keda.gulimall.goods.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnSingleCandidate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author Jmd
 * @create 2024-05-2024/5/15-10:03
 * @Description：
 */

@Configuration
public class RedisConfig {

    @Bean("redisTemplate")
    // @ConditionalOnSingleCandidate(RedisConnectionFactory.class) // 当应用的上下文中只有一个RedisConnectionFactory类型的对象时创建这个bean
    public RedisTemplate<Object,Object> redisTemplate(RedisConnectionFactory redisConnectionFactory){
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // json类型的序列化器，会将对象转换为json字符串
        /*
        *   redis中的序列化器，会将对象转换一定形式的字符串或者字节流
        *   不同的序列化器，序列化的方式不同，序列化器的选择很重要
        *   一般来说，key是用string类型的序列化器
        *
        *   Jackson2JsonRedisSerializer<>(Object.class) 序列化器在序列化时不会标注对象的类型
        *   GenericJackson2JsonRedisSerializer() 序列化器在序列化时会标注对象的类型，并且在反序列化时会将字符串转换为对应的类型
        * */
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setHashKeySerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        redisTemplate.afterPropertiesSet();

        return redisTemplate;
    }
}
