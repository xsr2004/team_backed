package com.yupao1.generate;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.yupao1.mapper.UserMapper;
import com.yupao1.model.domain.User;
import com.yupao1.utils.SpringContextUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

/**
 * @Author：xsr
 * @Date：2023/9/23 21:37
 * 用户名生成器
 */
@Component
public class usernameGenerator {
    public static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";//可选字符
    public static final int USERNAME_LENGTH = 8;//用户名长度
    public static final String USERNAME_PREFIX="游客";//用户名前缀
    public static final String USERNAME_COLUMN_IN_DATABASE ="username";//在数据库中用户名的列名
    private static final UserMapper userMapper=(UserMapper) SpringContextUtils.getBean(UserMapper.class);
    //随机生成字符串
    private static String generateRandomString(){
        //根据 CHARACTERS ，随机生成length长度的字符串
        Random random = new Random();
        StringBuilder sb = new StringBuilder(USERNAME_LENGTH);
        for (int i = 0; i < usernameGenerator.USERNAME_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        sb.insert(0,USERNAME_PREFIX);
        return sb.toString();
    }
    //该用户名是否存在
    private static boolean isUsernameExists(String userName){
        QueryWrapper<User> userWrapper = new QueryWrapper<>();
        userWrapper.eq(USERNAME_COLUMN_IN_DATABASE,userName);
        return userMapper.exists(userWrapper);
    }
    public static String generateUniqueUsername(){
        String s = generateRandomString();
        while (isUsernameExists(s)){
            s=generateRandomString();
        }
        return s;
    }
}
