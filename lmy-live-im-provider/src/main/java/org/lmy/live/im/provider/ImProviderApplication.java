package org.lmy.live.im.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImProviderApplication
//        implements CommandLineRunner
{

//    @Resource
//    private ImTokenService imTokenService;

    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(ImProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);
    }

//    @Override
//    public void run(String... args) throws Exception {
//        long userId=10213123L;
//        String token=imTokenService.createImLoginToken(userId, AppIdEnum.LMY_LIVE_BIZ.getCode());
//        System.out.println("token is :"+token);
//        Long userIdByToken = imTokenService.getUserIdByToken(token);
//        System.out.println("userId is :"+userIdByToken);
//    }
}
