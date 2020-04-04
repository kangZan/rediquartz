package com.demo.rediquartzdemo.quartz;

import indi.kang.rediquartz.quartz.job.support.redis.RedisMultipleNodesUniJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

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

    private StringRedisTemplate stringRedisTemplate;

    @Autowired(required = false)
    public void setRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        RedisSerializer stringSerializer = new StringRedisSerializer();
        stringRedisTemplate.setKeySerializer(stringSerializer);
        stringRedisTemplate.setValueSerializer(stringSerializer);
        stringRedisTemplate.setHashKeySerializer(stringSerializer);
        stringRedisTemplate.setHashValueSerializer(stringSerializer);
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public static volatile ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();

    @Override
    public void startTask(String jobName, String group) throws Exception {
        if (map.containsKey(jobName)) {
            log.error("重复执行错误！！！！！！！！！！");
            throw new Exception("重复执行错误！！！！！！！！！！");
        }
        map.put(jobName, jobName);
        log.info("job jobName:" + jobName);
        log.info("job group:" + group);
    }
}
