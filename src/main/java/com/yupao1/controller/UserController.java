package com.yupao1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao1.exception.BusinessException;
import com.yupao1.common.BaseResponse;
import com.yupao1.contant.enums.ErrorCode;
import com.yupao1.utils.ResultUtils;
import com.yupao1.model.domain.User;
import com.yupao1.model.domain.request.UserLoginRequest;
import com.yupao1.model.domain.request.UserRegisterRequest;
import com.yupao1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.yupao1.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5000",allowCredentials = "true")
@Slf4j
public class UserController {

    @Resource
    private UserService userService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 用户注册
     *
     * @param userRegisterRequest
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        // 校验
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        String planetCode = userRegisterRequest.getPlanetCode();
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            return null;
        }
        long result = userService.userRegister(userAccount, userPassword, checkPassword, planetCode);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @param request
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request, HttpServletResponse response) {
        if (userLoginRequest == null) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword, request);
        String sessionId = request.getSession().getId();
        Cookie cookie = new Cookie("JSESSIONID", sessionId); // JSESSIONID 是默认的 Session ID 名称
//        cookie.setHttpOnly(true); // 设置 HttpOnly 属性，防止脚本攻击
        // 可以设置其他 Cookie 的属性，比如过期时间等
        cookie.setMaxAge(1);
        // 将 Cookie 添加到响应头中
        response.addCookie(cookie);
        if(user==null){
            return ResultUtils.error(ErrorCode.NULL_ERROR,"用户名或密码错误");
        }
        return ResultUtils.success(user);
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @GetMapping("/logout")
    public BaseResponse<Integer> userLogout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        int result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 获取当前用户
     *
     * @param request
     * @return
     */
    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request) {
        //从request中获取当前session
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        long userId = currentUser.getId();
        // TODO 校验用户是否合法
        User user = userService.getById(userId);
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @GetMapping("/search")
    public BaseResponse<List<User>> searchUser(String username, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username", username);
        }
        List<User> userList = userService.list(queryWrapper);
        List<User> list = userList.stream().map(user -> userService.getSafetyUser(user)).collect(Collectors.toList());
        return ResultUtils.success(list);
    }
    /* recommendUser
    * 首页推荐用户
    * @param pageSize pageSize 页面个数
     * @param pageNum pageNum  第几页
     * @param request request
    * @return com.yupao1.common.BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.yupao1.model.domain.User>>
    * @throws Exception
    * @author
    * @date 2023/9/22
    */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUser(long pageSize,long pageNum,HttpServletRequest request){
        //查缓存，缓存格式为xzh:user:recommend:userID
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        String redisKey = String.format("xzh:user:recommend:%s", userService.getLoginUser(request).getId());
        Page<User> userPage = (Page<User>)ops.get(redisKey);
        if(userPage!=null){
            return ResultUtils.success(userPage);
        }
        //查数据库
        //todo 这里应该将具体业务处理操作放入service中
        //todo 推荐的用户应该是处理后的，不是直接从数据库拿来的，应该有个匹配
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        userPage = userService.page(new Page<>((pageNum - 1) *pageSize, pageSize), wrapper);
        //写缓存
        try {
            ops.set(redisKey,userPage,10000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            log.error("redis写入缓存失败！",e);
        }
        return ResultUtils.success(userPage);
    }
    /** matchUsers
    * 匹配相似度最高的num条用户
    * @param num num
     * @param request request
    * @return com.yupao1.common.BaseResponse<java.util.List<com.yupao1.model.domain.User>>
    * @throws Exception
    * @author
    * @date 2023/8/15
    */
    @GetMapping("/match")
    public BaseResponse<List<User>> matchUsers(long num,HttpServletRequest request){
        //num合法校验
        //1.[0,20]之内
        if(num<=0||num>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.matchUsers(num,loginUser));
    }
    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchUserByTags(@RequestParam List<String> tagNameList){
        if(CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<User> userList = userService.searchUserByTags(tagNameList);
        return ResultUtils.success(userList);
    }
    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody User user,HttpServletRequest request){

        //user判空
        if(user==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //获取登录用户
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //进入业务处理
        int result = userService.updateUser(user, loginUser);
        return ResultUtils.success(result);
    }
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(@RequestBody long id, HttpServletRequest request) {
        if (!userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }




}
