package com.yupao1.once;

import com.yupao1.model.domain.User;
import com.yupao1.service.UserService;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;

/**
 * @Author：xsr
 * @name：insertUser
 * @Date：2023/7/31 18:50
 * @Filename：insertUser
 */
@Component
public class insertUser {
    @Resource
    private UserService userService;
    /** insertUsers
    *
    * 插入假数据100万条
    * @return void
    * @throws Exception
    * @author
    * @date 2023/7/31
    */
    public void insertUsers(){
        //调用userService封装的批量插入100万条假数据
        final int ONE_INSERT_NUM=1000;
        ArrayList<User> list = new ArrayList<>();
        for(int i=0;i<ONE_INSERT_NUM;i++){
            User user = new User();
            user.setId((long) (i + 1));
            user.setUsername("xzh"+(i+1));
            user.setUserAccount("xzh"+(i+1));
            user.setAvatarUrl("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF");
            user.setDescription("一名java开发者，专注于java搬砖和api调用的精神小伙，阿拉啦啦啦啦");
            user.setGender(0);
            user.setUserPassword("b1c6328f959a4bbaca1ee3fe6637ccfc");//加密后的
            user.setPhone("18892273574");
            user.setEmail("xsr2004217@gmail.com");
            user.setUserStatus(0);
            user.setCreateTime(new Date());
            user.setUpdateTime(new Date());
            user.setIsDelete(0);
            user.setUserRole(0);
            user.setPlanetCode("1111");
            user.setTags("[\"java\",\"c++\",\"python\",\"男\"]");
            list.add(user);
        }
        userService.saveBatch(list,100);
    }
}
