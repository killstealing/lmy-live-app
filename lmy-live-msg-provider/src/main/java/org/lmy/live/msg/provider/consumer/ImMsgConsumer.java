package org.lmy.live.msg.provider.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.msg.provider.consumer.handler.MessageHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ImMsgConsumer implements InitializingBean {

    private static final Logger logger= LoggerFactory.getLogger(ImMsgConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

    @Resource
    private MessageHandler messageHandler;

    //记录每个用户连接的IM 服务器地址，然后根据IM服务器的链接地址去做具体机器的调用
    //基于MQ广播思路去做，可能会有消息风暴发生，100台机器，99%的mq消息都是无效的
    //加入一个叫路由层的设计，router中转的设计，router就是一个dubbo的rpc层
    //A-->B im-core-server --> msg-provider(持久化) -->router -->im-core-server -->通知到b
    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName());
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(10);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听IM发送过来的业务消息topic
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.LMY_LIVE_IM_BIZ_MSG_TOPIC,"");
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            for (MessageExt messageExt:msgs) {
                byte[] body = messageExt.getBody();
                ImMsgBodyDTO imMsgBodyDTO= JSON.parseObject(new String(body),ImMsgBodyDTO.class);
                messageHandler.onReceive(imMsgBodyDTO);
                logger.info("[ImMsgConsumer] message: {}",imMsgBodyDTO);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        logger.info("mq消息消费者启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}
