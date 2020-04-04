package com.demo.rediquartzdemo.model;

import lombok.Data;

import java.util.Date;

/**
 * @program: rediquartz
 * @Description: TODO
 * @Version: 1.0
 * @History: 1.Created by zan.kang on 2020/4/4 20:58.
 * 2.
 **/
@Data
public class JobModel {
    private String jobName;
    private Date runDate;
    private String group;
}
