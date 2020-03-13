package indi.kang.rediquartz.quartz.job.support.redis;

import indi.kang.rediquartz.quartz.job.MultipleNodesUniJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;


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
    private RedisTemplate redisTemplate;


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
     * @param jobName   定时任务的jobname
     * @param group 定时任务的group
     */
    public void runTask(String jobName, String group) {
        log.debug("=============任务开始");
        log.debug("jobName:" + jobName);
        //放入执行标记到redis中的list并获取放入下标
        long flag = redisTemplate.opsForList().leftPush(jobName, jobName);
        //当下标为1证明是第一个放入的，可以执行任务
        if (flag == 1) {
            //执行实际任务
            try {
                startTask(jobName, group);
            } catch (Exception e) {
                log.error("MultipleNodesUniJobByRedis runTask key:{}", jobName);
                log.error("MultipleNodesUniJobByRedis runTask excption:{}", e);
            }
            //执行完毕清除redis执行标记
            redisTemplate.delete(jobName);
        } else {
            //当下标不为1证明是已有执行任务
            log.info(jobName + "第{}次重复执行", flag);
        }
        log.debug("=============任务结束");
    }


    /**
     * 实际任务执行
     * 实现需要实际执行任务job重写该方法
     *
     * @param jobName   定时任务的jobname
     * @param group 定时任务的group
     */
    public abstract void startTask(String jobName, String group);
}
