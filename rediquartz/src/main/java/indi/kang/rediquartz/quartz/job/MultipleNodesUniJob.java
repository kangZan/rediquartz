package indi.kang.rediquartz.quartz.job;


/**
 * @program: rediquartz
 * @Description: 多节点定时任务执行接口
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2019/9/13 23:04.
 * 2.
 **/
public interface MultipleNodesUniJob {
    void runTask(String jobName, String group);
}
