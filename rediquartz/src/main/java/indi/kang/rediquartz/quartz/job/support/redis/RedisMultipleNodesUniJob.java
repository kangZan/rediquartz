package indi.kang.rediquartz.quartz.job.support.redis;

import indi.kang.rediquartz.quartz.job.MultipleNodesUniJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * @program: rediquartz
 * @Description: 多节点定时任务执行redis实现类
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2019/9/14 20:14.
 * 2.
 **/
@Service
@Slf4j
public abstract class RedisMultipleNodesUniJob implements Job, MultipleNodesUniJob {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    /**
     * 由定时框架调用定时执行job
     */
    @Override
    public void execute(JobExecutionContext arg0) {
        String jobname = arg0.getJobDetail().getKey().getName();
        String group = arg0.getJobDetail().getKey().getGroup();
        runTask(jobname, group);
    }

    /**
     * 执行任务前从redis加锁
     * 手动直接执行job的方法
     *
     * @param jobName 定时任务的jobname
     * @param group   定时任务的group
     */
    public void runTask(String jobName, String group) {
        log.info("=============任务开始");
        log.info("jobName:" + jobName);
        //如果成功赋值则15分钟后（防止服务器时间差）清除redis执行标记，释放锁
        boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(jobName, jobName, 15L, TimeUnit.MINUTES);
        if (flag) {
            //执行实际任务
            try {
                startTask(jobName, group);
            } catch (Exception e) {
                log.error("MultipleNodesUniJobByRedis runTask key:{}", jobName);
                log.error("MultipleNodesUniJobByRedis runTask excption:{}", e);
                //业务发生异常立即释放锁
                stringRedisTemplate.delete(jobName);
            }
        } else {
            log.info(jobName + "已执行过，放弃执行");
        }
        log.info("=============任务结束");
    }


    /**
     * 实际任务执行
     * 实现需要实际执行任务job重写该方法
     *
     * @param jobName 定时任务的jobname
     * @param group   定时任务的group
     */
    public abstract void startTask(String jobName, String group) throws Exception;
}
