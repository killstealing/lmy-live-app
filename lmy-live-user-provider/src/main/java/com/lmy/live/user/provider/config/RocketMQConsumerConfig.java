package com.lmy.live.user.provider.config;

import com.alibaba.fastjson2.JSON;
import com.lmy.live.user.constants.CacheAsyncDeleteCode;
import com.lmy.live.user.constants.UserProviderTopicNames;
import com.lmy.live.user.dto.UserCacheAsyncDeleteDTO;
import com.lmy.live.user.dto.UserDTO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    private final static Logger logger= LoggerFactory.getLogger(RocketMQConsumerConfig.class);
    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + RocketMQConsumerConfig.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC, "");
        mqPushConsumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                String json = new String(msgs.get(0).getBody());
                UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(json, UserCacheAsyncDeleteDTO.class);
                if (CacheAsyncDeleteCode.USER_INFO_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                    Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    redisTemplate.delete(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
                    logger.info("延迟删除用户信息缓存，userId is {}",userId);
                } else if (CacheAsyncDeleteCode.USER_TAG_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                    Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    redisTemplate.delete(userProviderCacheKeyBuilder.buildUserTagKey(userId));
                    logger.info("延迟删除用户标签缓存，userId is {}",userId);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        logger.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
