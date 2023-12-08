package com.lmy.live.user.provider;

import com.lmy.live.user.constants.UserTagsEnum;
import com.lmy.live.user.provider.service.IUserTagService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 用戶中台服务提供者
 */
@SpringBootApplication
@EnableDubbo
@EnableDiscoveryClient
public class UserProviderApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private IUserTagService iUserTagService;
    @Override
    public void run(String... args) throws Exception {
        long userId=1002;
        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_RICH));
        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_VIP));
        System.out.println("当前用户是否拥有VIP标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_VIP));
        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
        System.out.println("当前用户是否拥有OLD_USER 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_OLD_USER));

//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_VIP));
//        System.out.println("当前用户是否拥有VIP标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_VIP));
//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_OLD_USER));
//        System.out.println("当前用户是否拥有OLD_USER 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_OLD_USER));
    }
}
