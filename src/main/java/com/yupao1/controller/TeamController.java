package com.yupao1.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao1.common.BaseResponse;
import com.yupao1.contant.enums.ErrorCode;
import com.yupao1.utils.ResultUtils;
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
import com.yupao1.service.UserService;
import com.yupao1.service.UserTeamService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author：xsr
 * @name：TeamController
 * @Date：2023/8/2 12:30
 * @Filename：TeamController
 */
@RestController
@RequestMapping("/team")
@CrossOrigin(origins = "http://localhost:5000",allowCredentials = "true")
@Slf4j
public class TeamController {
    @Resource
    private TeamService teamService;
    @Resource
    private UserService userService;
    @Resource
    private UserTeamService userTeamService;
    /** addTeam
    *
    * @param team team
    新增队伍
    * @return com.yupao1.common.BaseResponse<java.lang.Long>
    * @throws Exception
    * @author
    * @date 2023/8/2
    */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody Team team, HttpServletRequest request){
        if(team==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //检测用户登录
        User loginUser = userService.getLoginUser(request);
        long l = teamService.addTeam(team, loginUser);
        return ResultUtils.success(l,"创建队伍成功，返回team的id");
    }
    /** deleteTeam
    *
    * @param teamId teamId
    根据id删除team
    * @return com.yupao1.common.BaseResponse<java.lang.Boolean>
    * @throws Exception
    * @author
    * @date 2023/8/2
    */
    @GetMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(Long teamId){
        if(teamId==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        boolean remove = teamService.removeById(teamId);
        if(!remove){
            log.error("删除id={}的team失败",teamId);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }
    /** updateTeam 
    * 更新队伍
    * @param teamUpdateRequest teamUpdateRequest
     * @param request request
    * @return com.yupao1.common.BaseResponse<java.lang.Boolean>
    * @throws Exception
    * @author 
    * @date 2023/8/3
    */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,HttpServletRequest request){
        if(teamUpdateRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        boolean update = teamService.updateTeam(teamUpdateRequest,loginUser);
        if(!update){
            log.error("来自TeamController的错误：更新id={}的team失败",teamUpdateRequest.getId());
            throw new BusinessException(ErrorCode.SYSTEM_ERROR);
        }
        return ResultUtils.success(true);
    }
    /** queryOneTeam
    *
    * @param teamId teamId
    根据id查询一个team
    * @return com.yupao1.common.BaseResponse<com.yupao1.model.domain.Team>
    * @throws Exception
    * @author
    * @date 2023/8/2
    */
    @GetMapping("/query/one")
    public BaseResponse<Team> queryOneTeam(long teamId){
        if(teamId<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(teamId);
        if(team==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(team);
    }
    /** queryListTeam
    *
    * @param teamQuery teamQuery
    根据teamQuery查询符合条件的team
    * @return com.yupao1.common.BaseResponse<java.util.List<com.yupao1.model.domain.Team>>
    * @throws Exception
    * @author
    * @date 2023/8/2
    */
    @PostMapping("/query/list")
    public BaseResponse<List<TeamUserVO>> queryListTeam(@RequestBody TeamQuery teamQuery,HttpServletRequest request){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamList = teamService.listTeamUser(teamQuery,request);
        if(teamList==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamList);
    }
    /** queryMyCreateTeams
    * 查询我创建的队伍
     * @param request request
    * @return com.yupao1.common.BaseResponse<java.util.List<com.yupao1.model.domain.vo.TeamUserVO>>
    * @throws Exception
    * @author
    * @date 2023/8/8
    */
    @GetMapping("/query/list/myCreate")
    public BaseResponse<List<TeamUserVO>> queryMyCreateTeams(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userService.getLoginUser(request);
        TeamQuery OnlyQuery = new TeamQuery();
        OnlyQuery.setUserId(loginUser.getId());
        List<TeamUserVO> teamList = teamService.listTeamUser(OnlyQuery,request);
        if(teamList==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamList);
    }
    @GetMapping("/query/list/myJoin")
    public BaseResponse<List<TeamUserVO>> queryMyJoinTeams(HttpServletRequest request){
        if(request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //获取当前登录用户，先从userTeam中查出所有关于userId的记录
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        QueryWrapper<UserTeam> wrapper = new QueryWrapper<>();
        wrapper.eq("userId",userId);
        List<UserTeam> userTeamList = userTeamService.list(wrapper);
        //对list的userId进行分组，去重，然后封装到teamQuery，复用listTeamUser接口
        Map<Long, List<UserTeam>> listMap = userTeamList.stream().
                collect(Collectors.groupingBy(UserTeam::getTeamId));
        ArrayList<Long> idList = new ArrayList<>(listMap.keySet());
        TeamQuery OnlyQuery = new TeamQuery();
        OnlyQuery.setIdList(idList);
        List<TeamUserVO> teamList = teamService.listTeamUser(OnlyQuery,request);
        if(teamList==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(teamList);
    }
    /** queryListTeamByPage
    *
    * @param teamQuery teamQuery
    查询分页team列表
    * @return com.yupao1.common.BaseResponse<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.yupao1.model.domain.Team>>
    * @throws Exception
    * @author
    * @date 2023/8/2
    */
    @PostMapping("/query/list/page")
    public BaseResponse<Page<Team>> queryListTeamByPage(@RequestBody TeamQuery teamQuery){
        if(teamQuery==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //teamQuery封装team
        Team team = new Team();
        BeanUtils.copyProperties(teamQuery,team);
        QueryWrapper<Team> wrapper = new QueryWrapper<>();
        int pageNum = teamQuery.getPageNum();
        int pageSize = teamQuery.getPageSize();
        //分页查询
        Page<Team> page = new Page<>(pageNum,pageSize);
        Page<Team> result = teamService.page(page, wrapper);
        if(result==null){
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }
        return ResultUtils.success(result);
    }
    @PostMapping("/join")
    public BaseResponse<Boolean> joinTeam(@RequestBody JoinTeamRequest joinTeamRequest, HttpServletRequest request){
        if(joinTeamRequest==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean joinResult = teamService.joinTeam(joinTeamRequest,request);
        if(!joinResult){
            log.error("来自TeamController的错误：加入id={}的team失败",joinTeamRequest.getTeamId());
            throw new BusinessException(ErrorCode.NULL_ERROR,"加入队伍失败");
        }
        return ResultUtils.success(true);
    }
    /** quitTeam
    * 用户退出队伍
    * @param quitTeamRequest quitTeamRequest
     * @param request request
    * @return com.yupao1.common.BaseResponse<java.lang.Boolean>
    * @throws Exception
    * @author
    * @date 2023/8/3
    */
    @PostMapping("/quit")
    public BaseResponse<Boolean> quitTeam(@RequestBody QuitTeamRequest quitTeamRequest, HttpServletRequest request){
        if(quitTeamRequest==null||request==null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Boolean quit = teamService.quitTeam(quitTeamRequest, request);
        if(!quit){
            log.error("来自TeamController的错误：id={}的用户退出id={}的team失败",quitTeamRequest.getUserId(),quitTeamRequest.getTeamId());
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"退出队伍失败");
        }
        return ResultUtils.success(true);
    }
}
