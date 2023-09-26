package com.yupao1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupao1.mapper.TagMapper;
import com.yupao1.model.domain.Tag;
import com.yupao1.service.TagService;
import org.springframework.stereotype.Service;

/**
* @author 27512
* @description 针对表【tag(标签)】的数据库操作Service实现
* @createDate 2023-07-28 17:10:35
*/
@Service
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag>
    implements TagService{

}




