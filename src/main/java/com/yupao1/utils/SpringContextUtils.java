package com.yupao1.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @Author：xsr
 * @Date：2023/9/23 22:33
 * 全局工具包，可获取springboot管理的bean （不装配）
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {
    /**
    *spring 上下文对象
    */
    private static ApplicationContext applicationContext;
    /** setApplicationContext
    * 重写setApplicationContext方法，将对象赋值
    */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtils.applicationContext =applicationContext;
    }
    public static ApplicationContext getApplicationContext() {
        return SpringContextUtils.applicationContext;
    }
    /** getBean
    * 根据名字获取bean
    */
    public static Object getBean(String name){
        return getApplicationContext().getBean(name);
    }
    /** getBean
    * @author xsr
    * 根据类获取bean
    */
    public static <T> T getBean(Class<T> clazz){
        return getApplicationContext().getBean(clazz);
    }
}
