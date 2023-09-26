package com.yupao1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao1.model.domain.UserTeam;
import com.yupao1.service.UserTeamService;
import com.yupao1.mapper.UserTeamMapper;
import org.springframework.stereotype.Service;

/**
* @author 27512
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-08-02 12:14:19
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




