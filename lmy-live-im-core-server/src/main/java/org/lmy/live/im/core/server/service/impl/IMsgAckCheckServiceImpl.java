package org.lmy.live.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.idea.lmy.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.im.core.server.service.IMsgAckCheckService;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class IMsgAckCheckServiceImpl implements IMsgAckCheckService {
    private static final Logger logger= LoggerFactory.getLogger(IMsgAckCheckServiceImpl.class);

    @Resource
    private MQProducer mqProducer;

    @Resource
    private ImCoreServerProviderCacheKeyBuilder imCoreServerProviderCacheKeyBuilder;
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public void doMsgAck(ImMsgBodyDTO imMsgBodyDTO) {
        redisTemplate.opsForHash().delete(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(imMsgBodyDTO.getUserId(),imMsgBodyDTO.getAppId()),imMsgBodyDTO.getMsgId());
    }

    @Override
    public void recordMsgAck(ImMsgBodyDTO imMsgBodyDTO, int times) {
        redisTemplate.opsForHash().put(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(imMsgBodyDTO.getUserId(),imMsgBodyDTO.getAppId()),imMsgBodyDTO.getMsgId(),times);

    }

    @Override
    public void sendDelayMsg(ImMsgBodyDTO imMsgBodyDTO) {
        Message message=new Message();
        message.setBody(JSON.toJSONString(imMsgBodyDTO).getBytes());
        message.setTopic(ImCoreServerProviderTopicNames.LMY_LIVE_IM_ACK_MSG_TOPIC);
        //level1 is 1s, level 2 is 5s
        message.setDelayTimeLevel(2);
        try {
            SendResult sendResult = mqProducer.send(message);
            logger.info("[IMsgAckCheckServiceImpl] msg is {}, result is {}",imMsgBodyDTO,sendResult);
        } catch (Exception e) {
            logger.error("[IMsgAckCheckServiceImpl] error is "+e);
        }
    }

    @Override
    public int getMsgAckTimes(String msgId, Long userId, Integer appId) {
        Integer times= (Integer) redisTemplate.opsForHash().get(imCoreServerProviderCacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        if(times==null){
            return -1;
        }
        return times;
    }
}
