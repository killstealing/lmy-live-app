package org.lmy.live.im.router.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.provider.service.ImRouterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImRouterProviderApplication implements CommandLineRunner {


    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(ImRouterProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private ImRouterService imRouterService;

    @Override
    public void run(String... args) throws Exception {
        for (int i = 0; i < 1000; i++) {
            ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
            imMsgBodyDTO.setUserId(1001L);
            imRouterService.sendMsg(imMsgBodyDTO);
            Thread.sleep(3000);
        }
    }
}
