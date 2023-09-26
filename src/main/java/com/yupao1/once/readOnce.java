package com.yupao1.once;

import com.alibaba.excel.EasyExcel;

/**
 * @Author：xsr
 * @name：readToDatabase
 * @Date：2023/7/30 13:59
 * @Filename：readToDatabase
 */
public class readOnce {
    public static void main(String[] args) {
        String fileName = "D:\\idea_project\\yupao1_backed\\simpleWrite1690696711244.xlsx";
        // 这里 需要指定读用哪个class去读，然后读取第一个sheet 文件流会自动关闭
        EasyExcel.read(fileName, UserTableInfo.class, new UserInfoListener()).sheet().doRead();
    }

}
