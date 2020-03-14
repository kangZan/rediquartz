package indi.kang.rediquartz.quartz.scheduler;

import org.quartz.SchedulerException;

import java.util.Date;
import java.util.List;

/**
 * @program: rediquartz
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2020/3/14 9:55.
 * 2.
 **/
public interface Schedulers {
    /**
     * 增加定时执行任务
     *
     * @throws SchedulerException
     */
    void startJob(String jobName, String groupName, Date date, Class jobClass) throws Exception;

    /**
     * 删除某个任务
     *
     * @param name
     * @param group
     */
    void deleteJob(String name, String group);

    /**
     * 获取定时Job列表
     *
     * @return
     * @throws SchedulerException
     */
    List getJobDetail() throws Exception;
}
