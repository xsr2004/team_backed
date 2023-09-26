package com.yupao1.model.domain.request;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author：xsr
 * 分页请求配置
 * @name：PageRequest
 * @Date：2023/8/2 13:38
 * @Filename：PageRequest
 */
@Data
public class PageRequest implements Serializable {


    private static final long serialVersionUID = -8050271600296256012L;

    protected int pageNum=1;
    protected int pageSize=10;
}
