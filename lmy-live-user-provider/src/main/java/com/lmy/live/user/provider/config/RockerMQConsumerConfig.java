package com.lmy.live.user.provider.config;

import com.alibaba.fastjson2.JSON;
import com.lmy.live.user.constants.CacheAsyncDeleteCode;
import com.lmy.live.user.constants.UserProviderTopicNames;
import com.lmy.live.user.dto.UserCacheAsyncDeleteDTO;
import com.lmy.live.user.dto.UserDTO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.ConsumeResult;
import org.apache.rocketmq.client.apis.consumer.FilterExpression;
import org.apache.rocketmq.client.apis.consumer.FilterExpressionType;
import org.apache.rocketmq.client.apis.consumer.PushConsumer;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.Collections;

@Configuration
public class RockerMQConsumerConfig implements InitializingBean {

    private final static Logger logger= LoggerFactory.getLogger(RockerMQConsumerConfig.class);
    @Resource
    private RockerMQConsumerProperties rockerMQConsumerProperties;
    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        initConsumer();
    }
    private void initConsumer(){
        try {
            final ClientServiceProvider provider = ClientServiceProvider.loadService();
            ClientConfiguration clientConfiguration = ClientConfiguration.newBuilder()
                    .setEndpoints(rockerMQConsumerProperties.getNameSrv())
                    .build();
            // 订阅消息的过滤规则，表示订阅所有Tag的消息。
            String tag = "*";
            FilterExpression filterExpression = new FilterExpression(tag, FilterExpressionType.TAG);
            // 为消费者指定所属的消费者分组，Group需要提前创建。
            String consumerGroup = "lmy-live-cache-delete";
            // 指定需要订阅哪个目标Topic，Topic需要提前创建。
            String topic = UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC;
            // 初始化PushConsumer，需要绑定消费者分组ConsumerGroup、通信参数以及订阅关系。
            PushConsumer pushConsumer = provider.newPushConsumerBuilder()
                    .setClientConfiguration(clientConfiguration)
                    // 设置消费者分组。
                    .setConsumerGroup(consumerGroup)
                    // 设置预绑定的订阅关系。
                    .setSubscriptionExpressions(Collections.singletonMap(topic, filterExpression))
                    // 设置消费监听器。
                    .setMessageListener(messageView -> {
                        // Handle the received message and return consume result.
                        logger.info("Consume message={}", messageView);

                        Charset charset = Charset.forName("utf-8");
                        CharBuffer charBuffer = charset.decode(messageView.getBody());
                        String msgStr = charBuffer.toString();
                        logger.info("consumer msgStr {}",msgStr);
                        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(msgStr, UserCacheAsyncDeleteDTO.class);

                        if(userCacheAsyncDeleteDTO!=null&&userCacheAsyncDeleteDTO.getCode()== CacheAsyncDeleteCode.USER_INFO_DELETE.getCode()){
                            Long userId1 = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                            String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId1);
                            redisTemplate.delete(key);
                            logger.error("延迟删除User info处理,userId is {}",userId1);
                        }else if(userCacheAsyncDeleteDTO.getCode()==CacheAsyncDeleteCode.USER_TAG_DELETE.getCode()){
                            Long userId1 = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                            String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId1);
                            redisTemplate.delete(key);
                            logger.error("延迟删除User tag处理,userId is {}",userId1);
                        }
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
            logger.info("mq 消费者启动成功,nameSrv is {}",rockerMQConsumerProperties.getNameSrv());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
