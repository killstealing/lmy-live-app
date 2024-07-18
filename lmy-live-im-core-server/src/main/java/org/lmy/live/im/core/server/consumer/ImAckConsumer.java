package org.lmy.live.im.core.server.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.lmy.live.im.core.server.service.IMsgAckCheckService;
import org.lmy.live.im.core.server.service.IRouterHandlerService;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class ImAckConsumer implements InitializingBean {

    private static final Logger logger= LoggerFactory.getLogger(ImAckConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;

    @Resource
    private IMsgAckCheckService iMsgAckCheckService;

    @Resource
    private IRouterHandlerService iRouterHandlerService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer defaultMQPushConsumer=new DefaultMQPushConsumer();
        defaultMQPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"_"+ImAckConsumer.class.getSimpleName());
        defaultMQPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
        defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        defaultMQPushConsumer.subscribe(ImCoreServerProviderTopicNames.LMY_LIVE_IM_ACK_MSG_TOPIC,"");
        defaultMQPushConsumer.setMessageListener((MessageListenerConcurrently)(msgs, context)->{
            MessageExt messageExt1 = msgs.get(0);
            byte[] body = messageExt1.getBody();
            ImMsgBodyDTO imMsgBodyDTO= JSON.parseObject(new String(body),ImMsgBodyDTO.class);
            int msgAckTimes = iMsgAckCheckService.getMsgAckTimes(imMsgBodyDTO.getMsgId(), imMsgBodyDTO.getUserId(), imMsgBodyDTO.getAppId());
            logger.info("[ImAckConsumer] msgAckTimes: {}",msgAckTimes);
            if(msgAckTimes<0){
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }else if(msgAckTimes<2){
                iMsgAckCheckService.recordMsgAck(imMsgBodyDTO,msgAckTimes+1);
                iMsgAckCheckService.sendDelayMsg(imMsgBodyDTO);
                iRouterHandlerService.sendMessageToClient(imMsgBodyDTO);
            }else{
                iMsgAckCheckService.doMsgAck(imMsgBodyDTO);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        defaultMQPushConsumer.start();
        logger.info("mq消息消费者启动，nameSrv is {}",rocketMQConsumerProperties.getNameSrv());
    }
}
