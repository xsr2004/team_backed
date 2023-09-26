package com.yupao1.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @Author：xsr
 * @name：DemoDate
 * @Date：2023/7/30 13:28
 * @Filename：DemoDate
 */
@Data
@EqualsAndHashCode
public class UserTableInfo {
    @ExcelProperty("昵称")
    private String username;
    @ExcelProperty("账号")
    private String userAccount;
    @ExcelProperty("头像")
    private String avatarUrl;
    @ExcelProperty("性别")
    private Integer gender;
    @ExcelProperty("密码")
    private String userPassword;
    @ExcelProperty("电话")
    private String phone;
    @ExcelProperty("邮箱")
    private String email;
    @ExcelProperty("状态")
    private Integer userState;
    @ExcelProperty("是否删除")
    private Integer isDelete;
    @ExcelProperty("用户角色")
    private Integer userRole;
    @ExcelProperty("创建时间")
    private Date createTime;
    @ExcelProperty("更新时间")
    private Date updateTime;
}
