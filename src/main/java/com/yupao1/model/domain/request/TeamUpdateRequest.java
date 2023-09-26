package com.yupao1.model.domain.request;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author：xsr
 * @name：TeamUpdateRequest
 * @Date：2023/8/3 11:50
 * @Filename：TeamUpdateRequest
 */
@Data
public class TeamUpdateRequest implements Serializable {
    private static final long serialVersionUID = 2859968352931102319L;
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
     * 最大人数
     */
    private Integer maxNum;
    /**
     * 队伍创建人
     */
    private Long userId;
    /**
     * 队伍密码
     */
    private String password;
    /**
     * 过期时间
     */
    private Date expireTime;
    /**
     * 0 - 公开，1 - 私有，2 - 加密
     */
    private Integer status;
}
