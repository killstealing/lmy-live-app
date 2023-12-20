package org.lmy.live.account.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

import java.util.concurrent.CountDownLatch;

@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class AccountProviderApplication {

    private static CountDownLatch countDownLatch = new CountDownLatch(1);

    public static void main(String[] args) throws InterruptedException {
        SpringApplication springApplication=new SpringApplication(AccountProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
        countDownLatch.await();
    }

//    @Resource
//    private IAccountTokenService accountTokenService;

//    @Override
//    public void run(String... args) throws Exception {
//        Long userId=123465L;
//        String token = accountTokenService.createAndSaveLoginToken(userId);
//        Long userIdByToken = accountTokenService.getUserIdByToken(token);
//        System.out.println("token:"+token);
//        System.out.println("userId:"+userIdByToken);
//    }
}
