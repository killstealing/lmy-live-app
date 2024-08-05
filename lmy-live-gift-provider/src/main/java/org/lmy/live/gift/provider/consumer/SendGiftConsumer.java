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
import org.lmy.live.gift.interfaces.constants.SendGiftTypeEnum;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 发送礼物消费者
 *
 * @Author idea
 * @Date: Created in 14:28 2023/8/1
 * @Description
 */
@Configuration
public class SendGiftConsumer implements InitializingBean {

    private static final Long MAX_PK_NUM=1000l;
    private static final Long MIN_PK_NUM=0l;


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
    private ILivingRoomRpc livingRoomRpc;
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
                Integer type = sendGiftMq.getType();
                AccountTradeRespDTO accountTradeRespDTO = accountRpc.consumeForSendGift(tradeReqDTO);
                JSONObject jsonObject=new JSONObject();
                //如果余额扣减成功
                if (accountTradeRespDTO.isSuccess()) {
                    if (type== SendGiftTypeEnum.DEFAULT_SEND_GIFT.getCode()){
                        //todo 出发礼物特效推送功能
                        jsonObject.put("url",sendGiftMq.getUrl());
                        sendGiftSingleton(sendGiftMq.getReceiverId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_SUCCESS.getCode(),jsonObject);
                    }else{
                        //pk类型的送礼 要通知什么给直播间的用户
                        //url 礼物特效全直播间可见
                        //todo 进度条全直播间可见
                        // 1000,进度条长度一共是1000，每个礼物对于进度条的影响就是一个数值（500（A）：500（B），550：450）
                        // 直播pk进度是不是以roomId为维度，string，送礼（A）incr，送礼给（B）就是decr。
                        Integer roomId = sendGiftMq.getRoomId();
                        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
                        Long objUserId=livingRoomRpc.queryOnlinePkUserId(roomId);
                        if(objUserId==null||livingRoomRespDTO==null||livingRoomRespDTO.getAnchorId()==null){
                            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                        }
                        Long pkUserId=livingRoomRespDTO.getAnchorId();
                        Long pkNum=0l;
                        String pkNumCacheKey = cacheKeyBuilder.buildPKNumCacheKey(roomId);
                        String pkNumSeqCacheKey = cacheKeyBuilder.buildPKNumSeqCacheKey(roomId);
                        Long pkNumSeqVal = redisTemplate.opsForValue().increment(pkNumSeqCacheKey);
                        if(sendGiftMq.getReceiverId().equals(pkUserId)){
                            // increase ,   500/500,   一个礼物50， 550/450
                            pkNum = redisTemplate.opsForValue().increment(pkNumCacheKey, sendGiftMq.getPrice());
                            if(pkNum>=MAX_PK_NUM){
                                jsonObject.put("winnerId", pkUserId);
                                pkNum=MAX_PK_NUM;
                            }
                        }else if(sendGiftMq.getUserId().equals(objUserId)){
                            pkNum= redisTemplate.opsForValue().decrement(pkNumCacheKey, sendGiftMq.getPrice());
                            if(pkNum<=MIN_PK_NUM){
                                jsonObject.put("winnerId", objUserId);
                                pkNum=MIN_PK_NUM;
                            }
                        }
                        jsonObject.put("sendGiftSeqNum",pkNumSeqVal);
                        jsonObject.put("pkNum",pkNum);
                        LivingRoomReqDTO livingRoomReqDTO=new LivingRoomReqDTO();
                        livingRoomReqDTO.setRoomId(sendGiftMq.getRoomId());
                        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
                        jsonObject.put("url",sendGiftMq.getUrl());
                        batchSendGift(userIdList,ImMsgBizCodeEnum.LIVING_ROOM_PK_SEND_GIFT_SUCCESS.getCode(), jsonObject);
                    }
                } else {
                    //todo 利用im将发送失败的消息告知用户
                    jsonObject.put("msg",accountTradeRespDTO.getMsg());
                    sendGiftSingleton(sendGiftMq.getUserId(), ImMsgBizCodeEnum.LIVING_ROOM_SEND_GIFT_FAIL.getCode(),jsonObject);
                }
                LOGGER.info("[SendGiftConsumer] msg is {}", msg);
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者:SendGiftConsumer启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }

    private void sendGiftSingleton(Long userId,int bizCode,JSONObject jsonObject){
        ImMsgBodyDTO imMsgBody=new ImMsgBodyDTO();
        imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
        imMsgBody.setBizCode(bizCode);
        imMsgBody.setUserId(userId);
        imMsgBody.setData(JSON.toJSONString(jsonObject));
        imRouterRpc.sendMsg(imMsgBody);
    }
    private void batchSendGift(List<Long> userIdList,int bizCode,JSONObject jsonObject){
        List<ImMsgBodyDTO> imMsgBodyDTOList = userIdList.stream().map(userId -> {
            ImMsgBodyDTO imMsgBody = new ImMsgBodyDTO();
            imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(bizCode);
            imMsgBody.setUserId(userId);
            imMsgBody.setData(JSON.toJSONString(jsonObject));
            return imMsgBody;
        }).collect(Collectors.toList());
        imRouterRpc.batchSendMsg(imMsgBodyDTOList);
    }
}
