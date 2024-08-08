package org.lmy.live.gift.provider.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.common.message.Message;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.common.interfaces.utils.ListUtils;
import org.lmy.live.gift.interfaces.constants.RedPacketStatusCodeEnum;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;
import org.lmy.live.gift.provider.dao.mapper.RedPacketConfigMapper;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;
import org.lmy.live.gift.provider.service.IRedPacketConfigService;
import org.lmy.live.gift.provider.service.bo.SendRedPacketBO;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.living.interfaces.dto.LivingRoomReqDTO;
import org.lmy.live.living.interfaces.rpc.ILivingRoomRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {
    private static final Logger logger = LoggerFactory.getLogger(RedPacketConfigServiceImpl.class);

    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @DubboReference
    private ILmyCurrencyAccountRpc accountRpc;

    @Resource
    MQProducer mqProducer;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusCodeEnum.NOT_PREPARE.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }

    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        //防止重复生成，以及错误参数传递情况
        RedPacketConfigPO redPacketConfigPO = this.queryByAnchorId(anchorId);
        if(redPacketConfigPO==null){
            return false;
        }
        String configCode = redPacketConfigPO.getConfigCode();
        String lockCacheKey = cacheKeyBuilder.buildRedPacketInitLock(configCode);
        boolean lockFlag = redisTemplate.opsForValue().setIfAbsent(lockCacheKey, 1, 3, TimeUnit.SECONDS);
        if(!lockFlag){
            return false;
        }
        Integer totalCount = redPacketConfigPO.getTotalCount();
        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        List<Integer> redPacketPriceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(configCode);
        //redis 输入输出缓冲区
        List<List<Integer>> splitList = ListUtils.splitList(redPacketPriceList, 100);
        for (List<Integer> list:splitList) {
            redisTemplate.opsForList().leftPushAll(cacheKey,list.toArray());
        }
        redisTemplate.expire(cacheKey,1,TimeUnit.DAYS);
        redPacketConfigPO.setStatus(RedPacketStatusCodeEnum.IS_PREPARE.getCode());
        this.updateById(redPacketConfigPO);
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(configCode), 1, 1, TimeUnit.DAYS);
        return true;
    }

    private List<Integer> createRedPacketPriceList(int totalPrice,int totalCount){
        List<Integer> redPacketList=new ArrayList<>(totalCount);
        int sum=0;
        for (int i = 0; i < totalCount; i++) {
            if(i+1==totalCount){
                sum+=totalPrice;
                redPacketList.add(totalPrice);
                break;
            }
            int avgPrice = totalPrice / (totalCount-i) * 2;
            int currentPrice= ThreadLocalRandom.current().nextInt(1,avgPrice);
            totalPrice-=currentPrice;
            redPacketList.add(currentPrice);
            sum+=currentPrice;
        }
//        System.out.println(sum);
        return redPacketList;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object cacheObj = redisTemplate.opsForList().rightPop(cacheKey);
        if (cacheObj == null) {
            return null;
        }
        Integer price = (Integer) cacheObj;
        logger.info("[receiveRedPacket] code is {},price is {}", code, price);
        SendRedPacketBO sendRedPacketBO = new SendRedPacketBO();
        sendRedPacketBO.setPrice(price);
        sendRedPacketBO.setReqDTO(reqDTO);
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.RECEIVE_RED_PACKET);
        message.setBody(JSON.toJSONBytes(sendRedPacketBO));
        try {
            SendResult sendResult = mqProducer.send(message);
            if (SendStatus.SEND_OK.equals(sendResult.getSendStatus())) {
                return new RedPacketReceiveDTO(price, "恭喜领取红包" + price + "旗鱼币");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new RedPacketReceiveDTO(null, "抱歉，红包被人抢走了，再试试？");
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        if (!redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code))) {
            return false;
        }
        String notifySuccessCache = cacheKeyBuilder.buildRedPacketNotify(code);
        if (redisTemplate.hasKey(notifySuccessCache)) {
            return false;
        }
        RedPacketConfigPO redPacketConfigPO = this.queryByConfigCode(code);
        LivingRoomReqDTO livingRoomReqDTO=new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
        List<Long> userIdListInRoom = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if(CollectionUtils.isEmpty(userIdListInRoom)){
            return false;
        }
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(redPacketConfigPO));
        this.batchSendImMsg(userIdListInRoom,ImMsgBizCodeEnum.START_RED_PACKET,jsonObject);
        redPacketConfigPO.setStatus(RedPacketStatusCodeEnum.HAS_SEND.getCode());
        this.updateById(redPacketConfigPO);
        redisTemplate.opsForValue().set(notifySuccessCache, 1, 1, TimeUnit.DAYS);
        return true;
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String configCode) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getConfigCode, configCode);
        queryWrapper.eq(RedPacketConfigPO::getStatus, RedPacketStatusCodeEnum.IS_PREPARE.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        List<ImMsgBodyDTO> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBodyDTO imMsgBody = new ImMsgBodyDTO();
            imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
            imMsgBody.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        routerRpc.batchSendMsg(imMsgBodies);
    }

    @Override
    public void receiveRedPacketHandle(RedPacketConfigReqDTO reqDTO,Integer price) {
        String code = reqDTO.getRedPacketConfigCode();
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalPrice(code);
        String totalGetCacheKey = cacheKeyBuilder.buildRedPacketTotalCount(code);
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPriceCache(reqDTO.getUserId()), price);
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        accountRpc.incr(reqDTO.getUserId(), price);
        redPacketConfigMapper.incrTotalGetPrice(code,price);
        redPacketConfigMapper.incrTotalGet(code);
    }


//    public static void main(String[] args) {
//        System.out.println(createRedPacketPriceList(1000,100));;
//        System.out.println(createRedPacketPriceList(1001,100));;
//        System.out.println(createRedPacketPriceList(1002,100));;
//        System.out.println(createRedPacketPriceList(2500,100));;
//    }
}
