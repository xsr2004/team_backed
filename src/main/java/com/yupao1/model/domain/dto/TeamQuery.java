package com.yupao1.model.domain.dto;

import com.baomidou.mybatisplus.annotation.TableLogic;
import com.yupao1.model.domain.request.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;
import java.util.List;

/**
 * @Author：xsr
 * team查询包装类
 * @name：TeamQueryRequest
 * @Date：2023/8/2 13:27
 * @Filename：TeamQueryRequest
 */
@Data
@EqualsAndHashCode(callSuper=true)//自动生成，比较时包含父类和子类本身
public class TeamQuery extends PageRequest {

    private static final long serialVersionUID = 1152946738070411544L;
    /**
     * 队伍id
     */
    private Long id;
    /**
     * 队伍idList
     */
    private List<Long> idList;
    /**
     * 队伍名称
     */
    private String name;
    /**
     * 搜索关键词
     */
    private String searchText;
    /**
     * 描述
     */
    private String description;
    /**
     * 最大人数
     */
    private Integer maxNum;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 用户id
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


}
