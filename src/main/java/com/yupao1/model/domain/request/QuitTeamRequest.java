package com.yupao1.model.domain.request;

import lombok.Data;

/**
 * @Author：xsr
 * 用户推出队伍请求参数
 * @name：QuitTeamRequest
 * @Date：2023/8/3 18:41
 * @Filename：QuitTeamRequest
 */
@Data
public class QuitTeamRequest {
    /*
    * 退出队伍的id
    */
    private Long teamId;
    /*
     * 操作者id
     */
    private Long userId;
    /*
     * 指定队长id
     */
    private Long newCaptainUserId;
}
