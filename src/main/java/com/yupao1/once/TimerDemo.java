package com.yupao1.once;

import java.util.Timer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @Author：xsr
 * @name：TimerTask
 * @Date：2023/8/1 11:22
 * @Filename：TimerTask
 */
public class TimerDemo {
    public static void main(String[] args) {
//        1.util的Timer
//        Timer timer = new Timer();
//        timer.schedule(new java.util.TimerTask() {
//            @Override
//            public void run() {
//                System.out.println("TimerTask被执行");
//            }
//        },5000);
        // 创建一个定时任务调度器，使用线程池管理任务执行
        ScheduledExecutorService schedule = Executors.newScheduledThreadPool(2);
        Runnable task = new Runnable() {
            @Override
            public void run() {
                System.out.println("任务被执行");
            }
        };
        schedule.scheduleAtFixedRate(task,0,5, TimeUnit.SECONDS);
    }
}
