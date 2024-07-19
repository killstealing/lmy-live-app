//package com.lmy.live.user.provider.config;
//
//import jakarta.annotation.Resource;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.util.Random;
//import java.util.concurrent.ArrayBlockingQueue;
//import java.util.concurrent.ThreadFactory;
//
//@Configuration
//public class RocketMQProducerConfig {
//    private final static Logger logger= LoggerFactory.getLogger(RocketMQProducerConfig.class);
//    @Resource
//    private RockerMQProducerProperties rockerMQProducerProperties;
//
//    @Value("${spring.application.name}")
//    private String applicationName;
//
//    @Value("${lmy.rmq.topic}")
//    private String topic;
//
//
////    @Bean
////    public Producer producer(){
////        // 接入点地址，需要设置成Proxy的地址和端口列表，一般是xxx:8081;xxx:8081。
////        ClientServiceProvider provider = ClientServiceProvider.loadService();
////        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(rockerMQProducerProperties.getNameSrv());
////        ClientConfiguration configuration = builder.build();
////        // 初始化Producer时需要设置通信配置以及预绑定的Topic。
////        Producer producer = null;
////        try {
////            producer = provider.newProducerBuilder()
////                    .setTopics(topic)
////                    .setClientConfiguration(configuration)
////                    .build();
////        } catch (ClientException e) {
////            throw new RuntimeException(e);
////        }
////        logger.info("mq 生产者启动成功,nameSrv is {}",rockerMQProducerProperties.getNameSrv());
////        return producer;
////    }
//    @Bean
//    public MQProducer mqProducer(){
//        ThreadPoolExecutor asyncThreadPool = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 100, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(2000), new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r, "rocketmq-async-thread-" + new Random().ints().toString());
//            }
//        });
//        DefaultMQProducer defaultMQProducer = new DefaultMQProducer();
//        defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
//        defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameSrv());
//        defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
//        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProducerProperties.getRetryTimes());
//        defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
//        defaultMQProducer.setAsyncSenderExecutor(asyncThreadPool);
//        try {
//            defaultMQProducer.start();
//            LOGGER.info("mq生产者启动成功,namesrv is {}", rocketMQProducerProperties.getNameSrv());
//        } catch (MQClientException e) {
//            throw new RuntimeException(e);
//        }
//        return defaultMQProducer;
//    }
//}
