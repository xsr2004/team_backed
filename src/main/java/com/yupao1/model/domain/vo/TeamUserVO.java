package com.yupao1.model.domain.vo;

import com.yupao1.model.domain.User;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author：xsr
 * @name：TeamUserVO
 * @Date：2023/8/2 18:53
 * @Filename：TeamUserVO
 */
@Data
public class TeamUserVO implements Serializable {
    private static final long serialVersionUID = 6899820568778008266L;
    /**
     * 队伍id
     */
    private Long id;

    /**
     * 队伍名称
     */

    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 关键词
     */
    private String searchText;
    /**
     * 最大人数
     */
    private Integer maxNum;
    /**
    * 当前人数
    */
    private Integer currentNum;
    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 创建人id
     */
    private Long userId;

    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 创建人具体信息
     */
    private User CreateUser;
    /**
    * 队伍的成员
    */
    private List<User> userList;
}
