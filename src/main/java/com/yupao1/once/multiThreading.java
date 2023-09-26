package com.yupao1.once;
import java.util.Date;

import com.yupao1.model.domain.User;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

/**
 * @Author：xsr
 * @name：multiThreading
 * @Date：2023/7/31 21:55
 * @Filename：multiThreading
 */
public class multiThreading {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        1.实现Runnable接口
//        MyRunnable myRunnable = new MyRunnable();
        Thread thread1 = new Thread(new MyRunnable(),"线程1");
        Thread thread2 = new Thread(new MyRunnable(),"线程2");
        Thread thread3 = new Thread(new MyRunnable(),"线程3");
        thread1.setPriority(Thread.MAX_PRIORITY);
        thread2.setPriority(Thread.MIN_PRIORITY);
        thread3.setPriority(Thread.NORM_PRIORITY);//1>3>2
        thread1.setDaemon(true);
        thread1.start();
//        thread2.start();
//        thread3.start();
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //interrupt和interrupted？？？？
//        thread1.interrupt();

        System.out.println("main线程");
//        myRunnable.run=false;//合理中断
//        2.实现Callable接口
//        FutureTask<User> task = new FutureTask<User>(new MyCallable());
//        Thread thread = new Thread(task);
//        thread.start();
//        //可以获取该线程的run结果，不过会堵塞main线程，好处是有<T>，可以拿到线程执行结果
//        User user = task.get();
//        System.out.println(user);
//        System.out.println("main线程执行...");
    }

}
class MyRunnable implements Runnable{

    public boolean run=true;//向外暴露一个标志，让其更合理的终止线程而不丢失数据
    @Override
    public void run() {
        for (int i = 0; i < 100000000; i++) {
            if(run){
                try {
                    Thread.sleep(1000);
                    if(i==3){
                        Thread.yield();//当i=3时进行让步但可能再次被调度
                    }
                    System.out.println(Thread.currentThread().getName()+"执行中..."+i);
                } catch (InterruptedException e) {
                    System.out.println(Thread.currentThread().getName()+"被打断!");
                    throw new RuntimeException(e);
                }
            }else {
                System.out.println(Thread.currentThread().getName()+"即将终止...");
                //在这里可以save数据
                return;
            }
        }

    }
}
class MyCallable implements Callable<User>{

    @Override
    public User call() throws Exception {
        System.out.println(222);
        User user = new User();
        user.setId(0L);
        user.setUsername("");
        user.setUserAccount("");
        user.setAvatarUrl("");
        user.setDescription("");
        user.setGender(0);
        user.setUserPassword("");
        user.setPhone("");
        user.setEmail("");
        user.setUserStatus(0);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        user.setIsDelete(0);
        user.setUserRole(0);
        user.setPlanetCode("");
        user.setTags("");
        return user;
    }
}
