# Getting Started

## tips

 1.基于SpringBoot & Quartz & redis
 <br/> <br/>
 2.实现根据用户指定时间点的单次执行任务
  <br/> <br/>
 3.本处定时任务主要指：根据用户指定时间点的单次执行任务
  <br/> <br/>
 4.粗略实现在多节点执行定时任务时通过redis加锁：在多节点同时执行定时任务时只允许一个得到锁的服务执行任务。
 <br/> <br/>
 5.rediquartz项目中引用了  

       <!-lombok->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>
        <!--quartz-->
        <dependency>
            <groupId>org.quartz-scheduler</groupId>
            <artifactId>quartz</artifactId>
            <version>2.2.1</version>
        </dependency>
        <!--redis-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        
 引入到项目时可去除引用后使用项目自身的版本。
 lombok为快捷开发小工具，可自行去除
 6.在启动类添加注解将quartz纳入spring容器管理
 @ComponentScans(@ComponentScan("indi.kang.rediquartz.quartz"))


 