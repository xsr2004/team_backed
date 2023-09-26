package com.yupao1.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：xsr
 * 加入队伍请求封装类
 * @name：joinTeamRequest
 * @Date：2023/8/3 17:04
 * @Filename：joinTeamRequest
 */
@Data
public class JoinTeamRequest implements Serializable {
    private static final long serialVersionUID = 3997970800389620795L;
    /**
     * 队伍id
     */
    private Long teamId;
    /**
     * 操作用户id
     */
    private Long userId;
    /**
     * 队伍密码
     */
    private String password;
}
