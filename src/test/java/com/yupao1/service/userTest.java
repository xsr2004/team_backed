package com.yupao1.service;

import com.yupao1.model.domain.User;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

/**
 * @Author：xsr
 * @name：userTest
 * @Date：2023/7/28 17:34
 * @Filename：userTest
 */
@SpringBootTest
public class userTest {
    @Resource
    private UserService userService;
    @Test
    public void testSearchUserByTags(){
        List<String> tagList= Arrays.asList("java","python");
        List<User> users = userService.searchUserByTags(tagList);
        Assertions.assertNotNull(users);
    }

}
