package com.yupao1.service;

import com.yupao1.generate.usernameGenerator;
import org.junit.jupiter.api.Test;

/**
 * @Author：xsr
 * @Date：2023/9/23 22:02
 */
public class usernameGenertorTest {
    @Test
    public void testGenerator(){
        System.out.println(usernameGenerator.generateUniqueUsername());
    }

}
