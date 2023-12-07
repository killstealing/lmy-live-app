package com.lmy.live.user.provider.config;

import com.alibaba.fastjson2.JSON;
import com.lmy.live.user.dto.UserDTO;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.consumer.*;
import org.apache.rocketmq.client.apis.message.MessageView;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Collections;
import java.util.List;

@Configuration
public class RockerMQConsumerConfig implements InitializingBean {

    private final static Logger logger= LoggerFactory.getLogger(RockerMQConsumerConfig.class);
    @Resource
    private RockerMQConsumerProperties rockerMQConsumerProperties;
    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Value("${lmy.rmq.topic}")
    private String topic;

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
            String consumerGroup = "lmy-live-user-provider";
            // 指定需要订阅哪个目标Topic，Topic需要提前创建。
            String topic = "user-update-cache";
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

                        UserDTO userDTO= JSON.parseObject(msgStr,UserDTO.class);
                        if(userDTO==null||userDTO.getUserId()==null){
                            logger.error("用户 id 为空，参数异常，内容:{}", msgStr);
                        }
                        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
                        redisTemplate.delete(key);
                        logger.error("延迟删除处理,UserDTO is {}",userDTO);
                        return ConsumeResult.SUCCESS;
                    })
                    .build();
            logger.info("mq 消费者启动成功,nameSrv is {}",rockerMQConsumerProperties.getNameSrv());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
