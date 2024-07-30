package org.lmy.live.bank.provider;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class BankProviderApplication implements CommandLineRunner {
    private static final Logger logger= LoggerFactory.getLogger(BankProviderApplication.class);
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(BankProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }


    @Resource
    private ILmyCurrencyAccountService lmyCurrencyAccountService;


    @Override
    public void run(String... args) throws Exception {
        long userId = 10198123;
        lmyCurrencyAccountService.insertOne(userId);
        logger.info(""+lmyCurrencyAccountService.getByUserId(userId));
        lmyCurrencyAccountService.incr(userId,1000);
        lmyCurrencyAccountService.decr(userId,300);
        logger.info(""+lmyCurrencyAccountService.getByUserId(userId));
    }
}
