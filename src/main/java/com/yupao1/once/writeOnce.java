package com.yupao1.once;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.ListUtils;

import java.util.*;

/**
 * @Author：xsr
 * @name：writeOnce
 * @Date：2023/7/30 13:29
 * @Filename：writeOnce
 */

public class writeOnce {
    public static void main(String[] args) {
        // 写法1 JDK8+
        // since: 3.0.0-beta1
        String fileName = "simpleWrite" + System.currentTimeMillis() + ".xlsx";
        // 这里 需要指定写用哪个class去写，然后写到第一个sheet，名字为模板 然后文件流会自动关闭
        // 如果这里想使用03 则 传入excelType参数即可
        // 分页查询数据
        EasyExcel.write(fileName, UserTableInfo.class)
                .sheet("模板")
                .doWrite(writeOnce::data);

    }
    private static List<UserTableInfo> data() {
        List<UserTableInfo> list = ListUtils.newArrayList();
        Set<String> usedUsernames = new HashSet<>();
        Set<String> usedUserAccounts = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            UserTableInfo data = new UserTableInfo();
            //写死数据
            String username = generateUniqueUsername(usedUsernames);
            data.setUsername(username);

            // Generate unique userAccount
            String userAccount = generateUniqueUserAccount(usedUserAccounts);
            data.setUserAccount(userAccount);

            // Set common values
            data.setAvatarUrl("https://t7.baidu.com/it/u=1595072465,3644073269&fm=193&f=GIF");
            data.setGender(generateRandomBinaryValue());
            data.setUserPassword("somePassword");
            data.setPhone("somePhoneNumber");
            data.setEmail("someEmail@example.com");
            data.setUserState(generateRandomBinaryValue());
            data.setIsDelete(generateRandomBinaryValue());
            data.setUserRole(generateRandomBinaryValue());
            data.setCreateTime(new Date());
            data.setUpdateTime(new Date());
            list.add(data);
        }
        return list;
    }
    private static String generateUniqueUsername(Set<String> usedUsernames) {
        String username;
        do {
            username = generateRandomString(6); // Replace 6 with the desired length of the username
        } while (usedUsernames.contains(username));
        usedUsernames.add(username);
        return username;
    }

    // Helper method to generate a unique userAccount
    private static String generateUniqueUserAccount(Set<String> usedUserAccounts) {
        String userAccount;
        do {
            userAccount = generateRandomString(8); // Replace 8 with the desired length of the userAccount
        } while (usedUserAccounts.contains(userAccount));
        usedUserAccounts.add(userAccount);
        return userAccount;
    }

    // Helper method to generate a random binary value (0 or 1)
    private static int generateRandomBinaryValue() {
        return new Random().nextInt(2);
    }

    // Helper method to generate a random string of given length
    private static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int index = new Random().nextInt(characters.length());
            sb.append(characters.charAt(index));
        }
        return sb.toString();
    }
}
