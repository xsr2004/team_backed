package com.yupao1.service;

import com.yupao1.model.domain.Team;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yupao1.model.domain.User;
import com.yupao1.model.domain.dto.TeamQuery;
import com.yupao1.model.domain.request.JoinTeamRequest;
import com.yupao1.model.domain.request.QuitTeamRequest;
import com.yupao1.model.domain.request.TeamUpdateRequest;
import com.yupao1.model.domain.vo.TeamUserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 27512
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-08-02 12:12:13
*/
public interface TeamService extends IService<Team> {

    long addTeam(Team team, User LoginUser);

    List<TeamUserVO> listTeamUser(TeamQuery teamQuery, HttpServletRequest request);

    boolean updateTeam(TeamUpdateRequest teamUpdateRequest, User loginUser);

    Boolean joinTeam(JoinTeamRequest joinTeamRequest, HttpServletRequest request);

    Boolean quitTeam(QuitTeamRequest quitTeamRequest, HttpServletRequest request);
}
