package com.demo.rediquartzdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;

@SpringBootApplication
@ComponentScans(@ComponentScan("indi.kang.rediquartz.quartz"))
public class RediquartzdemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RediquartzdemoApplication.class, args);
    }

}
