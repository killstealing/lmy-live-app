package org.lmy.live.gift.provider.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.lmy.live.gift.provider.service.IRedPacketConfigService;
import org.lmy.live.gift.provider.service.bo.SendRedPacketBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * @Author idea
 * @Date: Created in 16:53 2023/9/23
 * @Description 收到红包后，修改账户信息操作
 */
@Component
public class ReceiveRedPacketConsumer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReceiveRedPacketConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + ReceiveRedPacketConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据更新的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.RECEIVE_RED_PACKET, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try {
                SendRedPacketBO sendRedPacketBO = JSON.parseObject(msgs.get(0).getBody(), SendRedPacketBO.class);
                redPacketConfigService.receiveRedPacketHandle(sendRedPacketBO.getReqDTO(), sendRedPacketBO.getPrice());
            }catch (Exception e) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("【ReceiveRedPacketConsumer】mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
