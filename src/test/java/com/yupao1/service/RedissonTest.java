package com.yupao1.service;

import com.yupao1.model.domain.User;
import org.junit.jupiter.api.Test;
import org.redisson.api.RList;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;

import javax.annotation.Resource;

/**
 * @Author：xsr
 * @name：RedissonTest
 * @Date：2023/8/1 18:51
 * @Filename：RedissonTest
 */
@SpringBootTest
public class RedissonTest {
    @Resource
    private RedissonClient redissonClient;
    @Test
    public void RedissonTest1(){
        RList<Object> list = redissonClient.getList("test-redisson-list");
        list.add("test1");
//        list.add(new User());
//        list.remove(0);
        System.out.println(list);
    }
}
