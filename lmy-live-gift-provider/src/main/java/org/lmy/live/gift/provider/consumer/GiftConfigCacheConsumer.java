package org.lmy.live.gift.provider.consumer;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.lmy.live.gift.provider.service.bo.GiftCacheRemoveBO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

@Configuration
public class GiftConfigCacheConsumer implements InitializingBean {

    private static final Logger logger= LoggerFactory.getLogger(GiftConfigCacheConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer consumer=new DefaultMQPushConsumer();
        //老版本中会开启，新版本的mq不需要使用到
        consumer.setVipChannelEnabled(false);
        consumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        consumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName()+"_"+GiftConfigCacheConsumer.class.getSimpleName());
        //一次从broker中拉取10条消息到本地内存当中进行消费
        consumer.setConsumeMessageBatchMaxSize(10);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        //监听礼物缓存数据跟更新的行为
        consumer.subscribe(GiftProviderTopicNames.REMOVE_GIFT_CACHE,"");
        consumer.setMessageListener((MessageListenerConcurrently)(msg, context)->{
            for (MessageExt messageExt : msg) {
                GiftCacheRemoveBO giftCacheRemoveBO= JSON.parseObject(new String(messageExt.getBody()), GiftCacheRemoveBO.class);
                if(giftCacheRemoveBO.isRemoveListCache()){
                    redisTemplate.delete(cacheKeyBuilder.buildGiftListCacheKey());
                }
                if(giftCacheRemoveBO.getGiftId()>0){
                    redisTemplate.delete(cacheKeyBuilder.buildGiftConfigCacheKey(giftCacheRemoveBO.getGiftId()));
                }
                logger.info("[GiftConfigCacheConsumer] remove gift cache");
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        logger.info("mq消费者:GiftConfigCacheConsumer启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
