package com.lmy.live.user.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger= LoggerFactory.getLogger(UserProviderApplication.class);
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(UserProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

//    @Resource
//    private IUserTagService iUserTagService;
//    @Resource
//    private IUserService userService;
//    @Resource
//    private IUserPhoneService userPhoneService;
    @Override
    public void run(String... args) throws Exception {


        //test 6-18  用户手机号注册登录RPC接口测试  start
//        String phone="17818723313";
//        UserLoginDTO userLoginDTO = userPhoneService.login(phone);
//        System.out.println("line 45:"+userLoginDTO);
//        System.out.println("line 46:"+userPhoneService.queryByUserId(userLoginDTO.getUserId()));
//        System.out.println("line 47:"+userPhoneService.queryByUserId(userLoginDTO.getUserId()));
//        System.out.println("line 48:"+userPhoneService.queryByPhone(phone));
//        System.out.println("line 49:"+userPhoneService.queryByPhone(phone));

        //test 6-18  用户手机号注册登录RPC接口测试  end







//        long userId= 1006L;

//        UserDTO userDTO = userService.getByUserId(userId);
//        userDTO.setNickName("test-nick-name");
//        userService.updateUserInfo(userDTO);
//
//        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println(iUserTagService.cancelTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有RICK标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//
//        CountDownLatch count=new CountDownLatch(1);
//        for (int i = 0; i < 100; i++) {
//            Thread thread=new Thread(()->{
//                try {
//                    count.await();
//                    logger.info("result is "+iUserTagService.setTag(userId, UserTagsEnum.IS_VIP));
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//            thread.start();
//        }
//        count.countDown();
//        Thread.sleep(100000);
//        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_VIP));
//        System.out.println("当前用户是否拥有VIP标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_VIP));
//        System.out.println(iUserTagService.setTag(userId, UserTagsEnum.IS_OLD_USER));
//        System.out.println("当前用户是否拥有OLD_USER 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_OLD_USER));

//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println("当前用户是否拥有RICH 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_RICH));
//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_VIP));
//        System.out.println("当前用户是否拥有VIP标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_VIP));
//        System.out.println(iUserTagService.cancelTag(userId,UserTagsEnum.IS_OLD_USER));
//        System.out.println("当前用户是否拥有OLD_USER 标签:"+iUserTagService.containTag(userId,UserTagsEnum.IS_OLD_USER));
    }
}
