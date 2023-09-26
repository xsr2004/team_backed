package com.yupao1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yupao1.exception.BusinessException;
import com.yupao1.service.UserService;
import com.yupao1.contant.enums.ErrorCode;
import com.yupao1.model.domain.User;
import com.yupao1.mapper.UserMapper;
import com.yupao1.utils.AlgorithmUtils;
import com.yupao1.generate.usernameGenerator;
import javafx.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.yupao1.contant.UserConstant.ADMIN_ROLE;
import static com.yupao1.contant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务实现类
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://yupi.icu">编程导航知识星球</a>
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User>
        implements UserService {

    @Resource
    private UserMapper userMapper;


    /**
     * 盐值，混淆密码
     */
    private static final String SALT = "yupi";

    /**
     * 用户注册
     *
     * @param userAccount   用户账户
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @param planetCode    星球编号
     * @return 新用户 id
     */
    @Override
    public long userRegister(String userAccount, String userPassword, String checkPassword, String planetCode) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword, checkPassword, planetCode)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }
        if (planetCode.length() > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "星球编号过长");
        }
        // 账户不能包含特殊字符（不能有?，等特殊符号）
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }
        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            return -1;
        }
        // 账户不能重复
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);

        long count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账号重复");
        }
        // 星球编号不能重复（这里指的是 用户自己在星球的编号
        queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("planetCode", planetCode);
        count = userMapper.selectCount(queryWrapper);
        if (count > 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编号重复");
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 3. 插入数据
        User user = new User();
        //自动生成用户名 ：模版 游客+随机6位数 todo
        user.setUsername(usernameGenerator.generateUniqueUsername());
        user.setUserAccount(userAccount);
        user.setUserPassword(encryptPassword);
        user.setPlanetCode(planetCode);
        boolean saveResult = this.save(user);
        if (!saveResult) {
            return -1;
        }
        return user.getId();
    }


    /**
     * 用户登录
     *
     * @param userAccount  用户账户
     * @param userPassword 用户密码
     * @param request
     * @return 脱敏后的用户信息
     */
    @Override
    public User userLogin(String userAccount, String userPassword, HttpServletRequest request) {
        // 1. 校验
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            return null;
        }
        if (userAccount.length() < 4) {
            return null;
        }
        if (userPassword.length() < 8) {
            return null;
        }
        // 账户不能包含特殊字符
        String validPattern = "[`~!@#$%^&*()+=|{}':;',\\\\[\\\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？]";
        Matcher matcher = Pattern.compile(validPattern).matcher(userAccount);
        if (matcher.find()) {
            return null;
        }
        // 2. 加密
        String encryptPassword = DigestUtils.md5DigestAsHex((SALT + userPassword).getBytes());
        // 查询用户是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", encryptPassword);
        User user = userMapper.selectOne(queryWrapper);
        // 用户不存在
        if (user == null) {
            log.info("user login failed, userAccount cannot match userPassword");
            return null;
        }
        // 3. 用户脱敏
        User safetyUser = getSafetyUser(user);
        // 4. 记录用户的登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);
        return safetyUser;
    }

    /**
     * 用户脱敏
     *
     * @param originUser
     * @return
     */
    @Override
    public User getSafetyUser(User originUser) {
        if (originUser == null) {
            return null;
        }
        User safetyUser = new User();
        //脱敏 操作
        //默认 都写上 todo 脱敏逻辑
        safetyUser.setId(originUser.getId());
        safetyUser.setUsername(originUser.getUsername());
        safetyUser.setUserAccount(originUser.getUserAccount());
        safetyUser.setAvatarUrl(originUser.getAvatarUrl());
        safetyUser.setDescription(originUser.getDescription());
        safetyUser.setGender(originUser.getGender());
        safetyUser.setPhone(originUser.getPhone());
        safetyUser.setEmail(originUser.getEmail());
        safetyUser.setPlanetCode(originUser.getPlanetCode());
        safetyUser.setUserRole(originUser.getUserRole());
        safetyUser.setUserStatus(originUser.getUserStatus());
        safetyUser.setCreateTime(originUser.getCreateTime());
        safetyUser.setTags(originUser.getTags());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        // 移除登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }
    /**
     * searchUserByTags
     * 根据标签查询用户
     *
     * @param tagList tagList
     * @return int
     * @throws Exception
     * @author
     * @date 2023/7/28
     */
    @Override
    public List<User> searchUserByTags(List<String> tagList){
        //1.sql拼接（简单）
        QueryWrapper<User> wrapper = new QueryWrapper<>();
//        //拼接 like 查询
        for(String tagName:tagList){
            wrapper = wrapper.like("tags", tagName);
        }
        List<User> userList = userMapper.selectList(wrapper);
//        userList.forEach(this::getSafetyUser);
        return userList.stream().map(this::getSafetyUser).collect(Collectors.toList());


//        2.内存查询（灵活）
//        QueryWrapper<User> wrapper = new QueryWrapper<>();
        //先查所有用户
//        List<User> userList = userMapper.selectList(wrapper);
//        在内存中判断是否包含要求的标签
//        for(User user:userList){
//
//
//        }
        //返回过滤不符合的
//        return userList.stream().filter(user -> {
//            String tags = user.getTags();
//            if(StringUtils.isNotBlank(tags)){
//                return false;
//            }
//            //反序列化->json转java对象。。。fastjson，gson，
//            Gson gson = new Gson();
//            Set<String> tagSet = gson.fromJson(tags, new TypeToken<Set<User>>() {
//            }.getType());
//            //这里的tagSet可能是null，所以可以用if判空，这里用optional.isNull
//            Set<String> tagSetValid = Optional.ofNullable(tagSet).orElse(new HashSet<>());
//
//            for(String tag:tagSetValid){
//                if(!tagSet.contains(tag)){
//                    return false;
//                }
//            }
//            return true;
//        }).map(this::getSafetyUser).collect(Collectors.toList());

    }

    @Override
    public User getLoginUser(HttpServletRequest request) {
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        if(userObj==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        return (User) userObj;
    }

    @Override
    public int updateUser(User user, User LoginUser) {
        Long userId = user.getId();
        //不是管理员并且不是本人修改
        if(!isAdmin(LoginUser)&& !Objects.equals(LoginUser.getId(), userId)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //查询user是否存在
        User oldUser = userMapper.selectById(userId);
        if(oldUser==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return userMapper.updateById(user);
    }

    @Override
    public boolean isAdmin(HttpServletRequest request) {
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }
    @Override
    public boolean isAdmin(User user) {
        return user != null && user.getUserRole() == ADMIN_ROLE;
    }

    @Override
    public List<User> matchUsers(long num, User loginUser) {
/*      查所有用户的id和tags，（此时的结果集只有id和tags）
        将loginUser的tags从json转为List<String>
        遍历每一个User，将User的tags和loginUser的tags对比，得到相似度，存到Pair<User,相似度>
        将pair从大到小排序，然后取前num条，取每一个pair的key id 得到一个 List<Long> 存储的就是matchUserList的id
        然后用sql in查询里面的id的User，然后将每一个结果集按id groupby分组，存到matchList
 */
        //todo 直接查出所有的user的id和tags（待优化）
        long begin = System.currentTimeMillis();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.select("id","tags");
        wrapper.isNotNull("tags");
        List<User> allUsersList = this.list(wrapper);
        //用一个可以进行比较排序的数据结构，这里用treeMap<用户列表的下标，相似度>
        // todo treemap怎么比较的:默认key排序 => 重写比较器 => lambda表达式 => Comparator.comparingInt => 不生效 => 换 pair
//        SortedMap<Integer, Long> treeMap = new TreeMap<>(Comparator.comparingInt(value -> value));
        //既然treeMap不行，就维护一个固定长度的pair<用户，相似度>的list，然后add进来。然后排序
        ArrayList<Pair<User, Long>> pairs = new ArrayList<>();
        String loginUserTags = loginUser.getTags();
        //创建一个gson对象为将tags字符串转为List<String>（TypeToken泛型转换器）
        Gson gson = new Gson();
        List<String> loginUserTagsList = gson.
                fromJson(loginUserTags, new TypeToken<List<String>>() {
        }.getType());
        //遍历 allUsersList 每一个user，然后和当前登录用户比较，得到一个相似度，最后比较
        for(int i=0;i<allUsersList.size();i++){
            User user = allUsersList.get(i);
            //取出user的tags
            String currentUserTags = user.getTags();
            //处理无标签和自己
            if(StringUtils.isBlank(currentUserTags)|| Objects.equals(user.getId(), loginUser.getId())){
                continue;
            }
            //将tags转为list
            List<String> currentUserTagsList = gson.fromJson(currentUserTags,
                    new TypeToken<List<String>>() {
            }.getType());
            //将user和loginUser的tagList进行比较得到相似度
            long distance = AlgorithmUtils.minDistance(currentUserTagsList, loginUserTagsList);
//            treeMap.put(i,distance);
            pairs.add(new Pair<>(user,distance));
        }
        // 按编辑距离由小到大排序

        List<Pair<User, Long>> topUserPairList = pairs.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
//        List<User> matchUserList = topUserPairList.stream().map(Pair::getKey).collect(Collectors.toList());

        //新建一个List<User>，遍历前num条，然后将其添加到List<User>中
        List<Long> matchUserIdList = topUserPairList.stream().map(pair->pair.getKey().getId()).collect(Collectors.toList());
        //根据matchUserList 查出匹配user的信息
        wrapper.clear();
        wrapper.in("id",matchUserIdList);
        //todo in打乱顺序了，如何排序 => 按id进行分组，维护一个map<用户Id,list<User>>，然后遍历这个map
//        因为是groupby，所以这里map的value是list<user> 但实际上不一定要这样写
        Map<Long, List<User>> matchUserIdUserList = this.list(wrapper).stream()
                .collect(Collectors.groupingBy(User::getId));
        ArrayList<User> matchUserList = new ArrayList<>();
        for(Long userId:matchUserIdList){
            matchUserList.add(matchUserIdUserList.get(userId).get(0));
        }
        long end = System.currentTimeMillis();
        System.out.println(end-begin);
        return matchUserList;
    }
}

