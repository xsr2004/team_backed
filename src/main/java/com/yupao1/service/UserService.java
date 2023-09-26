package com.yupao1.service;

import com.yupao1.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 用户服务
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
public interface UserService extends IService<User> {

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode);

    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    User getSafetyUser(User originUser);

    // [加入编程导航](https://t.zsxq.com/0emozsIJh) 深耕编程提升【两年半】、国内净值【最高】的编程社群、用心服务【20000+】求学者、帮你自学编程【不走弯路】

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);
    /**
     * searchUserByTags
     * 根据标签搜索用户
     *
     * @param tagList tagList
     * @return int
     * @throws Exception
     * @author
     * @date 2023/7/28
     */
    List<User> searchUserByTags(List<String> tagList);
    /** getLoginUser
    * 获取当前登录用户
    * @param request request

    * @return com.yupao1.model.domain.User
    * @throws Exception
    * @author
    * @date 2023/7/31
    */
    User getLoginUser(HttpServletRequest request);
    /** updateUser
    * 更新user信息
    * @param user user
     * @param LoginUser LoginUser
    * @return int
    * @throws Exception
    * @author
    * @date 2023/7/31
    */
    int updateUser(User user,User LoginUser);
    /**
     * 是否为管理员
     *  根据request判断管理员
     * @param request
     * @return
     */
     boolean isAdmin(HttpServletRequest request);
    /** isAdmin
    * 根据user判断管理员
    * @param user user

    * @return boolean
    * @throws Exception
    * @author
    * @date 2023/7/31
    */
    boolean isAdmin(User user);

    List<User> matchUsers(long num, User loginUser);
}
