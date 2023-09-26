package com.yupao1.service;
import java.util.Date;

import com.yupao1.model.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import javax.annotation.Resource;

/**
 * @Author：xsr
 * @name：redisTest
 * @Date：2023/8/1 14:23
 * @Filename：redisTest
 */
@SpringBootTest
public class redisTest {

    @Resource
    private RedisTemplate<String,Object> RedisTemplate;
    @Test
    public void redisTest1(){
        ValueOperations<String,Object> ops = RedisTemplate.opsForValue();
        //增
        ops.set("xzhString","xzh");
        ops.set("xzhDouble",2.0);
        ops.set("xzhInt",1);
        User user = new User();
        user.setId(0L);
        user.setUsername("");
        user.setUserAccount("");
        ops.set("xzhUser",user);

        //查
        Object xzhString = ops.get("xzhString");
        System.out.println(xzhString);
        Object xzhUser = ops.get("xzhUser");
        System.out.println(xzhUser);
    }
}
