package com.yupao1.service;

import com.yupao1.utils.AlgorithmUtils;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @Author：xsr
 * @name：AlgorithmTest
 * @Date：2023/8/15 10:37
 * @Filename：AlgorithmTest
 */
public class AlgorithmTest {
    @Test
    public void testUserName(){
        String xzh = "xzh";
        String xzhh = "xzhh";
        String xxzh = "xxzh";
        System.out.println(AlgorithmUtils.minDistance(xzh,xzhh));
        System.out.println(AlgorithmUtils.minDistance(xzh,xxzh));
        System.out.println(AlgorithmUtils.minDistance(xzhh,xxzh));
    }
    @Test
    public void testUserTags(){
        List<String> tagList1 = Arrays.asList("java", "大一", "男");
        List<String> tagList2 = Arrays.asList("java", "大二", "男");
        List<String> tagList3 = Arrays.asList("python", "大一", "男");
        List<String> tagList4 = Arrays.asList("python", "大一", "女");
        System.out.println(AlgorithmUtils.minDistance(tagList1,tagList2));
        System.out.println(AlgorithmUtils.minDistance(tagList1,tagList3));
        System.out.println(AlgorithmUtils.minDistance(tagList1,tagList4));

    }

}
