package com.lmy.live.user.provider.config;

import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Property;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.*;

@Configuration
public class RocketMQProducerConfig {
    private final static Logger logger= LoggerFactory.getLogger(RocketMQProducerConfig.class);
    @Resource
    private RockerMQProducerProperties rockerMQProducerProperties;

    @Value("${spring.application.name}")
    private String applicationName;

    @Value("${lmy.rmq.topic}")
    private String topic;


    @Bean
    public Producer producer(){
        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8081;xxx:8081。
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(rockerMQProducerProperties.getNameSrv());
        ClientConfiguration configuration = builder.build();
        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
        Producer producer = null;
        try {
            producer = provider.newProducerBuilder()
                    .setTopics(topic)
                    .setClientConfiguration(configuration)
                    .build();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        logger.info("mq 生产者启动成功,nameSrv is {}",rockerMQProducerProperties.getNameSrv());
        return producer;
    }

//    @Bean
//    public MQProducer mqProducer() {
//        ThreadPoolExecutor asyncThreadPoolExecutor = new ThreadPoolExecutor(100, 150, 3, TimeUnit.MINUTES, new ArrayBlockingQueue<>(1000),
//                new ThreadFactory() {
//                    @Override
//                    public Thread newThread(Runnable r) {
//                        Thread thread = new Thread(r);
//                        thread.setName(applicationName + ":rmq-producer:" + ThreadLocalRandom.current().nextInt(1000));
//                        return thread;
//                    }
//                });
//
////        ThreadPoolExecutor asyncThreadPoolExecutor = new
////                ThreadPoolExecutor(100, 50, 3, TimeUnit.MINUTES,
////                new ArrayBlockingQueue<>(1000), new
////                ThreadFactory() {
////                    @Override
////                    public Thread newThread(Runnable r) {
////                        Thread thread = new Thread(r);
////                        thread.setName(applicationName + ":rmq-producer:"
////                                + ThreadLocalRandom.current().nextInt(1000));
////                        return thread;
////                    }
////                });
//
//        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
//        try {
//            defaultMQProducer.setNamesrvAddr(rockerMQProducerProperties.getNameSrv());
//            defaultMQProducer.setProducerGroup(rockerMQProducerProperties.getGroupName());
//            defaultMQProducer.setRetryTimesWhenSendFailed(rockerMQProducerProperties.getRetryTimes());
//            defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rockerMQProducerProperties.getRetryTimes());
//            defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
//            defaultMQProducer.setAsyncSenderExecutor(asyncThreadPoolExecutor);
//            defaultMQProducer.start();
//            logger.info("mq 生产者启动成功,nameSrv is {}",rockerMQProducerProperties.getNameSrv());
//        } catch (MQClientException e) {
//            throw new RuntimeException(e);
//        }
//
//
//
//        return defaultMQProducer;
//    }
}
