package org.lmy.live.framework.mq.starter.producer;

import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.MQProducer;
import org.lmy.live.framework.mq.starter.properties.RocketMQProducerProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class RocketMQProducerConfig {
    private static final Logger logger= LoggerFactory.getLogger(RocketMQProducerConfig.class);
    @Resource
    private RocketMQProducerProperties rocketMQProducerProperties;

    @Bean
    public MQProducer mqProducer(){
        ThreadPoolExecutor asyncPoolExecutor=new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, 100, 30, TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000), new ThreadFactory() {
            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r,"rocketMq-async-thread-"+new Random().ints().toString());
            }
        });
        DefaultMQProducer defaultMQProducer=new DefaultMQProducer();
        defaultMQProducer.setProducerGroup(rocketMQProducerProperties.getGroupName());
        defaultMQProducer.setNamesrvAddr(rocketMQProducerProperties.getNameSrv());
        defaultMQProducer.setRetryTimesWhenSendFailed(rocketMQProducerProperties.getRetryTimes());
        defaultMQProducer.setRetryTimesWhenSendAsyncFailed(rocketMQProducerProperties.getRetryTimes());
        defaultMQProducer.setRetryAnotherBrokerWhenNotStoreOK(true);
        defaultMQProducer.setAsyncSenderExecutor(asyncPoolExecutor);
        try {
            defaultMQProducer.start();
        } catch (MQClientException e) {
            logger.error("[RocketMQProducerConfig] defaultMQProducer start has exception:{}",e);
            throw new RuntimeException(e);
        }
        return defaultMQProducer;
    }
}
