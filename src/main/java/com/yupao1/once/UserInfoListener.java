package com.yupao1.once;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.alibaba.excel.util.ListUtils;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;


/**
 * @Author：xsr
 * @name：UserInfoListener
 * @Date：2023/7/30 14:00
 * @Filename：UserInfoListener
 */
@Slf4j
public class UserInfoListener implements ReadListener<UserTableInfo> {
    /**
     * 每隔5条存储数据库，实际使用中可以100条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 100;

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param userTableInfo    one row value. Is is same as {@link AnalysisContext#readRowHolder()}
     * @param analysisContext
     */
    @Override
    public void invoke(UserTableInfo userTableInfo, AnalysisContext analysisContext) {
        Gson gson = new Gson();
        log.info("解析到一条数据:{}", gson.toJson(userTableInfo));

    }
    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param analysisContext
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {
        //存数据库？
        log.info("所有数据解析完成！");
    }
}
