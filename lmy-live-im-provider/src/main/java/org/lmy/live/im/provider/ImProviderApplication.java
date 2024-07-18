package org.lmy.live.im.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.provider.service.ImOnlineService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class ImProviderApplication
        implements CommandLineRunner
{

    private static final Logger logger= LoggerFactory.getLogger(ImProviderApplication.class);

//    @Resource
//    private ImTokenService imTokenService;

    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(ImProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.SERVLET);
        springApplication.run(args);
    }

    @Resource
    private ImOnlineService imOnlineService;

    @Override
    public void run(String... args) throws Exception {
        for (int i=0;i<10;i++){
            Long userId=1000L+i;
            logger.info("userId {} is online: {}",userId,imOnlineService.isOnline(userId, AppIdEnum.LMY_LIVE_BIZ.getCode()));
        }
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
