package com.yupao1.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
 
@Configuration
public class RedisConfig {
 
    /**
     * 自定义redistemplate,方便开发，避免问题
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(redisConnectionFactory);
        //采用redis自带的string序列化器
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        //String的key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        //Hash的key采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        return template;
    }
 
}