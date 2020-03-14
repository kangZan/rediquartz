package com.demo.rediquartzdemo.quartz;

import indi.kang.rediquartz.quartz.job.support.redis.RedisMultipleNodesUniJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @program: rediquartz
 * @Description: 定时输出任务job
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2020/3/13 23:43.
 * 2.
 **/
@Slf4j
@Service
public class SoutTaskJob extends RedisMultipleNodesUniJob {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public void startTask(String jobName, String group) {
        System.out.println("job jobName:" + jobName);
        System.out.println("job group:" + group);
    }
}
