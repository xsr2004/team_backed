package com.yupao1.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author：xsr
 * @name：Redisson
 * @Date：2023/8/1 18:21
 * @Filename：Redisson
 */
@Configuration
@ConfigurationProperties("spring.redis")
@Data
public class RedissonConfig{
    private String host;
    private String port;
    @Bean
    public RedissonClient RedissonClient(){
        //配置config
        Config config = new Config();
        String address = String.format("redis://%s:%s", host, port);
        config.useSingleServer()
                .setAddress(address)
                .setDatabase(3);
        //创建RedissonClient实例
        // Sync and Async API
        RedissonClient redisson = Redisson.create(config);
        return redisson;
    }


}
