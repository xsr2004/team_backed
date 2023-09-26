package com.yupao1.once;

import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author：xsr
 * @name：ThreadSynchronization
 * @Date：2023/8/1 10:52
 * @Filename：ThreadSynchronization
 */
public class ThreadSynchronization {
    public static void main(String[] args) {
        Account account = new Account("admin", 5000);
        AccountThread accountThread = new AccountThread(account);
        Thread thread1 = new Thread(accountThread, "线程1");
        Thread thread2 = new Thread(accountThread, "线程2");

        thread1.start();
        thread2.start();
    }
}
class Account{
    private String username;//账号
    private int balance;//余额
    private ReentrantLock lock=new ReentrantLock();
    public Account(String username, int balance) {
        this.username = username;
        this.balance = balance;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
    //取钱方法
    public void withDraw(int money){
        //同步？上锁？
        lock.lock();
        int before = getBalance();
            try {
                Thread.sleep(1000);

            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            //两个进程几乎同时启动，线程1休眠一秒，它还没来得及改变balance，线程2就已经getBalance了
        setBalance(before-money);
        lock.unlock();

    }

}
class AccountThread implements Runnable{
    private Account account;

    public AccountThread(Account account) {
        this.account = account;
    }

    @Override
    public void run() {
        //假设线程每次取500
        int money=500;
        account.withDraw(money);
        System.out.println(Thread.currentThread().getName() + "对"+account.getUsername()+"取款"+money+"成功，余额" + account.getBalance());
    }
}