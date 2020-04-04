package indi.kang.rediquartz.quartz.scheduler.support;

import indi.kang.rediquartz.quartz.job.support.redis.RedisMultipleNodesUniJob;
import indi.kang.rediquartz.quartz.scheduler.Schedulers;
import indi.kang.rediquartz.quartz.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.quartz.impl.triggers.CronTriggerImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @program: rediquartz
 * @Description: 定时任务操作类
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2019/9/12 21:00.
 * 2.
 **/
@Configuration
@Slf4j
public class QuartzSchedulers implements Schedulers { // 任务调度

    @Autowired
    private Scheduler scheduler;


    private void befor(String jobName, String groupName, Class jobClass, Date date) {
        log.info("=====================addQuartz jobClass: {}", jobClass);
        log.info("=====================jobName: {}", jobName);
        log.info("=====================groupName: {}", groupName);
        log.info("=====================date: {}  --->  '{}'", date, convertCron(date));
    }

    /**
     * 开始执行任务
     * 定时时间若已过去则现在执行
     *
     * @throws SchedulerException
     */
    public void startJob(String jobName, String groupName, Date date, Class jobClass) throws SchedulerException {
        if (System.currentTimeMillis() >= date.getTime()) {
            //当前时间大于定时时间则立刻执行
            //获取指定任务类
            RedisMultipleNodesUniJob multipleNodesUniJobByRedis = (RedisMultipleNodesUniJob) SpringContextUtils.getBean(jobClass);
            //手动调用执行任务
            multipleNodesUniJobByRedis.runTask(jobName, groupName);
            //当前时间大于定时时间则三分钟后执行
//            date = getTimeByMinute(3);
        } else {
            befor(jobName, groupName, jobClass, date);
            addJob(scheduler, jobName, groupName, date, jobClass);
            scheduler.start();
        }
    }
///**
// * 获取三分钟后的时间
// */
//    private Date getTimeByMinute(int minute) {
//        Calendar calendar = Calendar.getInstance();
//        calendar.add(Calendar.MINUTE, minute);
//        return calendar.getTime();
//    }


    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     */
    public void deleteJob(String name, String group) {
        try {
            JobKey jobKey = new JobKey(name, group);
            JobDetail jobDetail = scheduler.getJobDetail(jobKey);
            if (jobDetail == null)
                return;
            scheduler.deleteJob(jobKey);
        } catch (Exception ae) {
            log.error("quartzScheduler.startJob.delete :", ae);
        }
    }


    /**
     * 新增执行任务
     */
    private void addJob(Scheduler scheduler, String name, String group, Date date, Class jobClass) throws SchedulerException {
        // 通过JobBuilder构建JobDetail实例，JobDetail规定只能是实现Job接口的实例
        // JobDetail 是具体Job实例
        JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(name, group).build();
        // 基于表达式构建触发器
        String cron = convertCron(date);
        CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(cron);
        // CronTrigger表达式触发器 继承于Trigger
        // TriggerBuilder 用于构建触发器实例
        CronTrigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(name, group)
                .withSchedule(cronScheduleBuilder).build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    /**
     * 将日期转换为定时cron
     */
    private static String convertCron(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("00 mm HH dd MM ? yyyy");
        return sdf.format(date);
    }


    /**
     * 获取定时Job详细信息
     *
     * @return
     * @throws SchedulerException
     */
    public List getJobDetail() throws Exception {
        List result = new ArrayList();
        //获取jobkey信息
        scheduler.getJobKeys(GroupMatcher.anyGroup()).forEach((jobKey) -> {
            try {
                Map jobInfo = new HashMap<>();
                //jobname
                jobInfo.put("job", jobKey.getName());
                jobInfo.put("group", jobKey.getGroup());
                //定时器信息
                List cronTriggers = scheduler.getTriggersOfJob(jobKey);
                cronTriggers.forEach((cronTrigger) -> {
                    //生成时间
                    jobInfo.put("startTime", ((CronTriggerImpl) cronTrigger).getStartTime());
                    //下次运行时间
                    jobInfo.put("nextFireTime", ((CronTriggerImpl) cronTrigger).getNextFireTime());
                });
                result.add(jobInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return result;
    }

}

//

//
//    /**
//     * 修改某个任务的执行时间
//     *
//     * @param name
//     * @param group
//     * @param time
//     * @return
//     * @throws SchedulerException
//     */
//    public boolean modifyJob(String name, String group, String time) throws SchedulerException {
//        Date date = null;
//        TriggerKey triggerKey = new TriggerKey(name, group);
//        CronTrigger cronTrigger = (CronTrigger) scheduler.getTrigger(triggerKey);
//        String oldTime = cronTrigger.getCronExpression();
//        if (!oldTime.equalsIgnoreCase(time)) {
//            CronScheduleBuilder cronScheduleBuilder = CronScheduleBuilder.cronSchedule(time);
//            CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(name, group)
//                    .withSchedule(cronScheduleBuilder).build();
//            date = scheduler.rescheduleJob(triggerKey, trigger);
//        }
//        return date != null;
//    }
//
//    /**
//     * 暂停所有任务
//     *
//     * @throws SchedulerException
//     */
//    public void pauseAllJob() throws SchedulerException {
//        scheduler.pauseAll();
//    }
//
//    /**
//     * 暂停某个任务
//     *
//     * @param name
//     * @param group
//     * @throws SchedulerException
//     */
//    public void pauseJob(String name, String group) throws SchedulerException {
//        JobKey jobKey = new JobKey(name, group);
//        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//        if (jobDetail == null)
//            return;
//        scheduler.pauseJob(jobKey);
//    }
//
//    /**
//     * 恢复所有任务
//     *
//     * @throws SchedulerException
//     */
//    public void resumeAllJob() throws SchedulerException {
//        scheduler.resumeAll();
//    }
//
//    /**
//     * 恢复某个任务
//     *
//     * @param name
//     * @param group
//     * @throws SchedulerException
//     */
//    public void resumeJob(String name, String group) throws SchedulerException {
//        JobKey jobKey = new JobKey(name, group);
//        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
//        if (jobDetail == null)
//            return;
//        scheduler.resumeJob(jobKey);
//    }
