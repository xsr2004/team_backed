package com.yupao1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao1.contant.enums.ErrorCode;
import com.yupao1.contant.enums.TeamStatus;
import com.yupao1.exception.BusinessException;
import com.yupao1.model.domain.Team;
import com.yupao1.model.domain.User;
import com.yupao1.model.domain.UserTeam;
import com.yupao1.model.domain.dto.TeamQuery;
import com.yupao1.model.domain.request.JoinTeamRequest;
import com.yupao1.model.domain.request.QuitTeamRequest;
import com.yupao1.model.domain.request.TeamUpdateRequest;
import com.yupao1.model.domain.vo.TeamUserVO;
import com.yupao1.service.TeamService;
import com.yupao1.mapper.TeamMapper;
import com.yupao1.service.UserService;
import com.yupao1.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
* @author 27512
* @description 针对表【team(队伍)】的数据库操作Service实现
* @createDate 2023-08-02 12:12:13
*/
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team>
    implements TeamService{

    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User LoginUser) {
        //1. 请求参数是否为空？
        if(team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，是否是本人，否则不允许创建
        if(LoginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        if(!Objects.equals(LoginUser.getId(), team.getUserId())){
            throw new BusinessException(ErrorCode.NO_AUTH,"登录用户和创建者不是同一个人，请核验");
        }
        //3. 校验信息
        //   1. 队伍人数 > 1 且 <= 20
        if(team.getMaxNum()<1||team.getMaxNum()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数不符合规定，一般为1-20");
        }
        //   2. 队伍标题 <= 20
        if(team.getName().length()>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍名字不符合规定，一般<=20");
        }
        //   3. 描述 <= 512
        if(team.getDescription().length()>512){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍介绍不符合规定，一般<=512");
        }
        //   4. status 是否公开（int）不传默认为 0（公开）
        Integer status = team.getStatus();
        if(status==null){
            status=0;
        }
        TeamStatus teamStatusByValue = TeamStatus.getTeamStatusByValue(status);
        if (teamStatusByValue==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍状态不符合规定，一般为公开，私有，加密");
        }
        //   5. 如果 status 是加密状态，一定要有密码，且密码 <= 32
        if (teamStatusByValue.getValue()==2){
            if(StringUtils.isBlank(team.getPassword())||team.getPassword().length()>32){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密房间需设置密码，密码长度一般<=32");
            }
        }
        //   6. 超时时间 > 当前时间
        if(new Date().after(team.getExpireTime())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"过期时间设置错误");
        }
        //   7. 校验用户最多创建 5 个队伍，同时注意事务
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",LoginUser.getId());
        long count = userTeamService.count(wrapper);
        if(count>5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户只能创建5个队伍");
        }
        //4. 插入队伍信息到队伍表
        team.setId(null);//让plus自增
        team.setCreateTime(new Date());
        team.setUpdateTime(new Date());
        boolean save = this.save(team);
        if(!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"创建队伍失败");
        }
        //5. 插入用户  => 队伍关系到关系表
        UserTeam userTeam = new UserTeam();
        userTeam.setId(null);
        userTeam.setUserId(LoginUser.getId());
        userTeam.setTeamId(team.getId());
        userTeam.setJoinTime(new Date());
        save=userTeamService.save(userTeam);
        if(!save){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍-用户表插入失败");
        }
        return team.getId();
    }

    @Override
    public List<TeamUserVO> listTeamUser(TeamQuery teamQuery, HttpServletRequest request) {
        //1.判空
        if(teamQuery==null||request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2.判断登录
        if(userService.getLoginUser(request)==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //3.从请求参数中取出队伍名称等查询条件，如果存在则作为查询条件
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        Long teamId = teamQuery.getId();
        if(teamId!=null&&teamId>0){
            wrapper.eq("id",teamId);
        }
        List<Long> idList = teamQuery.getIdList();
        if(idList!=null&&idList.size()!=0){
            wrapper.in("id",idList);
        }
        String teamName = teamQuery.getName();
        if(teamName!=null){
            wrapper.like("name",teamName);
        }
        String description = teamQuery.getDescription();
        if(description!=null&&description.length()<512){
            wrapper.like("description",description);
        }
        String searchText = teamQuery.getSearchText();
        if(searchText!=null){
            wrapper.and(qw->qw.like("name",searchText).or().like("description",searchText));
        }
        //默认搜索公开或加密的，否则按传递的状态来
        Integer status = teamQuery.getStatus();
        if(status==null){
            wrapper.and(qw->qw.like("status",TeamStatus.PUBLIC.getValue()).or().like("status",TeamStatus.SECRET.getValue()));
        }else{
            wrapper.eq("status",status);
        }
        TeamStatus teamStatus = TeamStatus.getTeamStatusByValue(status);
        //如果是管理员则全部展示，否则去除私密队伍
        if(!userService.isAdmin(request)&&teamStatus==TeamStatus.PRIVATE){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        Integer maxNum = teamQuery.getMaxNum();
        if(maxNum!=null){
            wrapper.eq("maxNum",maxNum);
        }
        //默认筛选未过期队伍，若传递过期时间，则按其值进行附加
        Date expireTime = teamQuery.getExpireTime();
        if(expireTime!=null){
            wrapper.gt("expireTime",expireTime);
        }
        else{
            wrapper.gt("expireTime",new Date());
        }
//        wrapper.and(qw->qw.gt("expireTime",new Date()).or().isNull("expireTime"));
        Long CreateUser = teamQuery.getUserId();
        if(CreateUser!=null){
            wrapper.eq("userId",CreateUser);
        }
        List<Team> teamList = this.list(wrapper);
        if(CollectionUtils.isEmpty(teamList)){
            return new ArrayList<>();
        }
        ArrayList<TeamUserVO> teamUserVOList = new ArrayList<>();
        //5.关联查询创建人
        for (Team team:teamList){
            Long userId = team.getUserId();
            if(userId==null){
                continue;
            }
            //获取创建人
            User user = userService.getById(userId);
            //脱敏
            if(user!=null) {
                user = userService.getSafetyUser(user);
            }
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtils.copyProperties(team,teamUserVO);
            teamUserVO.setCreateUser(user);
            //回填
            teamUserVOList.add(teamUserVO);
        }
        return teamUserVOList;
        //6.关联查询已加入队伍的用户信息（sql优化！！）
        //select ut.id,ut.teamId
        //from team t,user_team ut
        //WHERE t.id=ut.teamId;

    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser) {
        //1. 判断请求参数是否为空
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //2. 查询队伍是否存在
        Long teamId = teamUpdateRequest.getId();
        Team oldTeam = this.getById(teamId);
        if(oldTeam==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //3. 只有管理员或者队伍的创建者可以修改
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }else if(!Objects.equals(teamUpdateRequest.getUserId(), loginUser.getId()) && !userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //4. 如果用户传入的新值和老值一致，就不用 update 了（可自行实现，降低数据库使用次数）
        //5. 如果队伍状态改为加密，必须要有密码，如果改为公开或隐私，则删除密码
        TeamStatus teamStatus = TeamStatus.getTeamStatusByValue(teamUpdateRequest.getStatus());
        if(teamStatus==TeamStatus.SECRET){
            if(StringUtils.isBlank(teamUpdateRequest.getPassword())){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"加密队伍需设置密码");
            }
        }
        //将参数进行打包，执行更新
        Team newTeam = new Team();
        BeanUtils.copyProperties(teamUpdateRequest,newTeam);
        if(teamStatus!=TeamStatus.SECRET){
            newTeam.setPassword(null);
        }
        //6. 更新成功
        boolean update = this.updateById(newTeam);
        return update;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request) {
        //对参数进行判空，登录判断
        if(joinTeamRequest==null||request==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        //登录判断
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //登录者和操作者同一？
        if(!Objects.equals(loginUser.getId(), joinTeamRequest.getUserId())){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //1. 用户最多加入 5 个队伍
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",loginUser.getId());
        if(userTeamService.count(wrapper)>=5){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"用户最多加入5个队伍");
        }
        //2. 队伍必须存在，只能加入未满、未过期的队伍
        Long teamId = joinTeamRequest.getTeamId();
        if(teamId==null||teamId<=-1){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数队伍id不合法");
        }
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该队伍不存在");
        }
        Date expireTime = team.getExpireTime();
        if(new Date().after(expireTime)){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍已过期");
        }
        wrapper=new QueryWrapper<>();
        wrapper.eq("teamId",teamId);
        if (userTeamService.count(wrapper)>=team.getMaxNum()){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"队伍人数已满");
        }
        //3. 不能加入自己的队伍，不能重复加入已加入的队伍（幂等性）
        if(Objects.equals(loginUser.getId(), team.getUserId())){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"不能重复加入自己的队伍");
        }
        wrapper=new QueryWrapper<>();
        wrapper.eq("teamId",teamId).eq("userId",loginUser.getId());
        UserTeam one = userTeamService.getOne(wrapper);
        if(one!=null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"你已加入该队伍");
        }
        //4. 禁止加入私密的队伍
        Integer status = team.getStatus();
        TeamStatus teamStatus = TeamStatus.getTeamStatusByValue(status);
        if(teamStatus==TeamStatus.PRIVATE&&!userService.isAdmin(loginUser)){
            throw new BusinessException(ErrorCode.NO_AUTH,"禁止加入私密队伍，请联系管理员");
        }
        //5. 如果加入的队伍是加密的，必须密码匹配才可以
        if(teamStatus==TeamStatus.SECRET){
            String password = joinTeamRequest.getPassword();
            if(StringUtils.isBlank(password)){
                throw new BusinessException(ErrorCode.PARAMS_ERROR,"该队伍为加密队伍，密码为空");
            }
            if(!password.equals(team.getPassword())){
                throw new BusinessException(ErrorCode.NO_AUTH,"队伍密码错误");
            }
        }
        //6. 新增队伍 - 用户关联信息
        //更新队伍人数
        team.setCurrentNum(team.getCurrentNum()+1);
        boolean update = this.updateById(team);
        if(!update){
            throw new BusinessException(ErrorCode.NULL_ERROR,"更新异常");
        }

        UserTeam userTeam = new UserTeam();
        userTeam.setTeamId(teamId);
        userTeam.setUserId(loginUser.getId());
        userTeam.setJoinTime(new Date());
        return userTeamService.save(userTeam);
    }

    @Override
    @Transactional
    public Boolean quitTeam(QuitTeamRequest quitTeamRequest, HttpServletRequest request) {
        //1. 校验请求参数
        if(quitTeamRequest==null||request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //2. 是否登录，登录者和操作者是否同一个人
        User loginUser = userService.getLoginUser(request);
        if(loginUser==null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        Long userId = quitTeamRequest.getUserId();
        if(!Objects.equals(loginUser.getId(), userId)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //3. 校验队伍是否存在
        Long teamId = quitTeamRequest.getTeamId();
        Team team = this.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"队伍不存在");
        }
        //4. 校验用户是否已加入队伍
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",userId).eq("teamId",teamId);
        UserTeam userTeam = userTeamService.getOne(wrapper);
        if(userTeam==null){
            throw new BusinessException(ErrorCode.NULL_ERROR,"该用户没有加入队伍");
        }
        //5. 如果队伍
        //   1. 如果是队长退出队伍，默认权限转移给第二早加入的用户 —— 先来后到，若指定队长则其成为队长
        if(Objects.equals(team.getUserId(), userId)){
            Long newUserId=null;
            if(quitTeamRequest.getNewCaptainUserId()==null){
                //> 只用取 id 最小的 2 条数据
                wrapper=new QueryWrapper<>();
                wrapper.eq("teamId",teamId).orderByAsc("joinTime").last("limit 2");
                List<UserTeam> userTeamList = userTeamService.list(wrapper);
                if(userTeamList.size()<=1){
                    //   3. 只剩一人时用户选择退出，自动销毁队伍
                    this.removeById(teamId);
                }else {
                    newUserId = userTeamList.get(1).getUserId();
                }
            }else {
                newUserId=quitTeamRequest.getNewCaptainUserId();
            }
            //尝试修改队伍队长，若newUserId为空则不修改userid。
            if(newUserId!=null){
                team.setUserId(newUserId);
            }

        }
//        //   2. 非队长，自己退出队伍
        //最后减去team的currentNum
        team.setCurrentNum(team.getCurrentNum()-1);

        boolean update = this.updateById(team);//这里update不了
        if(!update){
            throw new BusinessException(ErrorCode.NULL_ERROR,"更新异常");
        }
        return userTeamService.removeById(userTeam.getId());
    }

}




