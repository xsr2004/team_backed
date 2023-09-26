package com.yupao1.jobs;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yupao1.model.domain.User;
import com.yupao1.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author：xsr
 * @name：PreRedisScheduler
 * @Date：2023/8/1 15:48
 * @Filename：PreRedisScheduler
 * 缓存预热
 */
@Component
@Slf4j
public class PreRedisScheduler {
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserService userService;
    @Resource
    private RedissonClient redissonClient;
    private List<Long> userList= Arrays.asList(1L,2L,3L,4L,5L,6L,7L,8L);//白名单
    @Scheduled(cron = "0 0 16 * * *")
    public void doPreRedisScheduler(){
        //Redisson 分布式锁
        //从redis中获取锁 key为xzh:user:Redisson:%lock，
        RLock lock = redissonClient.getLock("xzh:user:Redisson:lock");
        //尝试获取锁，设置等待时间为0，即获取不到就放弃，释放时间为30秒，即获取锁后执行30秒
        try {
            if(lock.tryLock(0, 30000, TimeUnit.MILLISECONDS)){
                //获取到锁，执行定时程序30秒
                for(long id:userList){
                    //查数据库
                    ValueOperations<String, Object> ops = redisTemplate.opsForValue();
                    String redisKey = String.format("xzh:user:recommend:%s", id);
                    Page<User> userPage = (Page<User>)ops.get(redisKey);
                    QueryWrapper<User> wrapper = new QueryWrapper<>();
                    userPage = userService.page(new Page<>(1, 8), wrapper);
                    //写缓存
                    try {
                        ops.set(redisKey,userPage,1, TimeUnit.DAYS);
                    } catch (Exception e) {
                        log.error("redis写入缓存失败！",e);
                    }
                }
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }finally {
            //最终释放锁，
            //确保释放的是自己的锁

            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
        //续期，看门狗机制：
        // 开一个监听线程，默认给你30s时间去执行自己的任务，10秒后若没执行完则续费到30s，一共循环n次
        // 注意debug下看门狗会当做服务器宕机直接挂掉不续期。

    }
}
