package com.demo.rediquartzdemo.quartz;

import com.demo.rediquartzdemo.model.JobModel;
import indi.kang.rediquartz.quartz.scheduler.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;

/**
 * @program: rediquartz
 * @Description: 定时任务开机系统自启
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2020/3/13 23:46.
 * 2.
 **/

@Slf4j
@Component//被spring容器管理
@Order(3)//如果多个自定义ApplicationRunner，用来标明执行顺序
public class QuartzStartupBySelf implements ApplicationRunner {

    @Autowired
    private Schedulers quartzSchedulers;

    @Override
    public void run(ApplicationArguments applicationArguments) throws Exception {
        try {
            runTask();
        } catch (Exception e) {
            log.error("=====================runTask :{}", e);
        }
    }

    /**
     * 系统启动时循环任务列表加入定时器
     */
    private void runTask() {
        //找出需要执行的定时任务
        List<JobModel> jobDOList = getJobDOList();
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                final int j = i;
                jobDOList.forEach(job -> {
                    //定时任务的key
                    //定时任务的group
                    try {
                        job.setGroup("group" + j);
                        quartzSchedulers.startJob(job.getJobName(), job.getGroup(), job.getRunDate(), SoutTaskJob.class);
                    } catch (Exception e) {
                        log.error("=====================startJob :{}", e);
                        log.error("=====================param :{}", job);
                    }
                });
            }
        }).start();


    }

    /**
     * 临时获取定时任务实体
     *
     * @return
     */
    private List<JobModel> getJobDOList() {
        List<JobModel> jobDOList = new LinkedList<JobModel>();
        for (int i = 0; i < 10; i++) {
            JobModel job = new JobModel();
            //执行时间
            job.setRunDate(localDateTimeToDate(LocalDateTime.now().plusMinutes(1 + i)));
            //定时任务的key
            job.setJobName("jobName" + i);
            jobDOList.add(job);
        }
        /*
         *构建一个立即执行的job
         * **/
        JobModel runNowjob = new JobModel();
        //执行时间
        runNowjob.setRunDate(new Date());
        //定时任务的key
        runNowjob.setJobName("nowJobName");
        //定时任务的group
        runNowjob.setGroup("nowGroup");
        jobDOList.add(runNowjob);
        return jobDOList;
    }

    /**
     * LocalDateTime转换为Date
     *
     * @param localDateTime
     */
    private Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }


}
