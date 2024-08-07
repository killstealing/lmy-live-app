package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ListUtils;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;
import org.lmy.live.gift.provider.dao.mapper.RedPacketConfigMapper;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;
import org.lmy.live.gift.provider.service.IRedPacketConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {
    private static final Logger logger = LoggerFactory.getLogger(RedPacketConfigServiceImpl.class);

    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
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
        String lockCacheKey = cacheKeyBuilder.buildRedPacketInitLock(redPacketConfigPO.getConfigCode());
        boolean lockFlag = redisTemplate.opsForValue().setIfAbsent(lockCacheKey, 1, 3, TimeUnit.SECONDS);
        if(!lockFlag){
            return false;
        }
        Integer totalCount = redPacketConfigPO.getTotalCount();
        Integer totalPrice = redPacketConfigPO.getTotalPrice();
        List<Integer> redPacketPriceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(redPacketConfigPO.getConfigCode());
        //redis 输入输出缓冲区
        List<List<Integer>> splitList = ListUtils.splitList(redPacketPriceList, 100);
        for (List<Integer> list:splitList) {
            redisTemplate.opsForList().leftPushAll(cacheKey,list);
        }
        redPacketConfigPO.setStatus(CommonStatusEum.INVALID_STATUS.getCode());
        this.updateById(redPacketConfigPO);

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
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalPrice(code);
        String totalGetCacheKey = cacheKeyBuilder.buildRedPacketTotalCount(code);
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPriceCache(reqDTO.getUserId()),price);
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey, price);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        //todo lua脚本去记录最大值
        logger.info("[receiveRedPacket] code is {},price is {}", code, price);
        return new RedPacketReceiveDTO(price);
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

        return true;
    }


//    public static void main(String[] args) {
//        System.out.println(createRedPacketPriceList(1000,100));;
//        System.out.println(createRedPacketPriceList(1001,100));;
//        System.out.println(createRedPacketPriceList(1002,100));;
//        System.out.println(createRedPacketPriceList(2500,100));;
//    }
}
