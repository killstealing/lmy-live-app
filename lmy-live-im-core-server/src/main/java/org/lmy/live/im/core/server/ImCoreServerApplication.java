package org.lmy.live.im.core.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImCoreServerApplication {
    //指定监听的端口
    //基于netty去启动一个java进程，绑定监听的端口
    //netty初始化相关的handler
    //打印日志，方便观察
    //基于JVM的钩子函数去实现优雅的关闭

    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(ImCoreServerApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
