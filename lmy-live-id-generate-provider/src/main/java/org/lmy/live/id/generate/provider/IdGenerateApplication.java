package org.lmy.live.id.generate.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.lmy.live.id.generate.provider.service.IdGenerateService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class IdGenerateApplication implements CommandLineRunner {

    @Resource
    private IdGenerateService idGenerateService;

    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(IdGenerateApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Override
    public void run(String... args) {
        Set<Long> set=new HashSet<>();
        for (int i = 0; i < 1300; i++) {
            Long id= idGenerateService.getUnSeqId(1);
            System.out.println(i+":"+id);
            set.add(id);
        }
        System.out.println("set size:"+set.size());
    }
}
