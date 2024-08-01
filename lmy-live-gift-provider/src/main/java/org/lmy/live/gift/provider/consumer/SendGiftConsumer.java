package org.lmy.live.gift.provider.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.common.interfaces.dto.SendGiftMq;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * 发送礼物消费者
 *
 * @Author idea
 * @Date: Created in 14:28 2023/8/1
 * @Description
 */
@Configuration
public class SendGiftConsumer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendGiftConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference
    private ILmyCurrencyAccountRpc accountRpc;
    @DubboReference
    private ImRouterRpc imRouterRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + SendGiftConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存当中进行消费
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据更新的行为
        mqPushConsumer.subscribe(GiftProviderTopicNames.SEND_GIFT, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                SendGiftMq sendGiftMq = JSON.parseObject(new String(msg.getBody()), SendGiftMq.class);
                String cacheKey = cacheKeyBuilder.buildGiftConsumeCacheKey(sendGiftMq.getUuid());
                boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, -1, 5, TimeUnit.MINUTES);
                if(!lockStatus){
                    //代表曾经消费过
                    continue;
                }
                AccountTradeReqDTO tradeReqDTO = new AccountTradeReqDTO();
                tradeReqDTO.setUserId(sendGiftMq.getUserId());
                tradeReqDTO.setNum(sendGiftMq.getPrice());
                AccountTradeRespDTO accountTradeRespDTO = accountRpc.consumeForSendGift(tradeReqDTO);
                ImMsgBodyDTO imMsgBody=new ImMsgBodyDTO();
                JSONObject jsonObject=new JSONObject();
                imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                //如果余额扣减成功
                if (accountTradeRespDTO.isSuccess()) {
                    //todo 出发礼物特效推送功能
                    imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode());
                    imMsgBody.setUserId(sendGiftMq.getReceiverId());
                    jsonObject.put("url",sendGiftMq.getUrl());
                } else {
                    //todo 利用im将发送失败的消息告知用户
                    imMsgBody.setBizCode(ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode());
                    imMsgBody.setUserId(sendGiftMq.getUserId());
                    jsonObject.put("msg",accountTradeRespDTO.getMsg());
                }
                imMsgBody.setData(JSON.toJSONString(jsonObject));
                imRouterRpc.sendMsg(imMsgBody);
                LOGGER.info("[SendGiftConsumer] msg is {}", msg);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者:SendGiftConsumer启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
