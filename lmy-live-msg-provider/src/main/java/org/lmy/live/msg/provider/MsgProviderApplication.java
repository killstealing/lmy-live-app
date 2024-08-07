package org.lmy.live.msg.provider;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
@EnableDubbo
public class MsgProviderApplication
//        implements CommandLineRunner
{

    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(MsgProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

//    @Resource
//    private ISmsService iSmsService;

//    @Override
//    public void run(String... args) throws Exception {

//        MsgSendResultEnum msgSendResultEnum = iSmsService.sendMessage("17665753022");
//        System.out.println(msgSendResultEnum);

//        String phone="17665753022";
//        MsgSendResultEnum msgSendResultEnum = iSmsService.sendMessage(phone);
//        System.out.println(msgSendResultEnum);
//        while(true){
//            System.out.println("输入验证码");
//            Scanner scanner=new Scanner(System.in);
//            int code=scanner.nextInt();
//            MsgCheckDTO msgCheckDTO = iSmsService.checkLoginCode(phone, code);
//            System.out.println(msgCheckDTO);
//        }
//    }
}
