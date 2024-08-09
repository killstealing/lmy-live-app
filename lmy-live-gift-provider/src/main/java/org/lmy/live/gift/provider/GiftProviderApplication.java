package org.lmy.live.gift.provider;


import jakarta.annotation.Resource;
import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.lmy.live.gift.interfaces.rpc.ISkuStockInfoRPC;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDubbo
public class GiftProviderApplication implements CommandLineRunner {
    public static void main(String[] args) {
        SpringApplication springApplication=new SpringApplication(GiftProviderApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }

    @Resource
    private ISkuStockInfoRPC skuStockInfoRPC;

    @Override
    public void run(String... args) throws Exception {
        Long anchorId = 45601L;
        Long skuId = 90713L;
        skuStockInfoRPC.prepareStockInfo(anchorId);
        for(int i=0;i<11;i++) {
            boolean decrStatus = skuStockInfoRPC.decrStockNumBySkuIdV2(skuId,10);
            System.out.println(decrStatus);
        }
    }

}
