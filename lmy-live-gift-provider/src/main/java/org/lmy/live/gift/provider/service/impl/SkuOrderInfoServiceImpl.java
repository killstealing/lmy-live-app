package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoRespDTO;
import org.lmy.live.gift.provider.dao.mapper.SkuOrderInfoMapper;
import org.lmy.live.gift.provider.dao.po.SkuOrderInfoPO;
import org.lmy.live.gift.provider.service.ISkuOrderInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 07:12 2023/10/16
 * @Description
 */
@Service
public class SkuOrderInfoServiceImpl implements ISkuOrderInfoService {

    @Resource
    private SkuOrderInfoMapper skuOrderInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;


    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        String cacheKey = cacheKeyBuilder.buildSkuOrder(userId, roomId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        if (cacheObj != null) {
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        LambdaQueryWrapper<SkuOrderInfoPO> qw = new LambdaQueryWrapper<>();
        qw.eq(SkuOrderInfoPO::getUserId, userId);
        qw.eq(SkuOrderInfoPO::getRoomId, roomId);
        qw.orderByDesc(SkuOrderInfoPO::getId);
        qw.last("limit 1");
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectOne(qw);
        if (skuOrderInfoPO != null) {
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO,60, TimeUnit.MINUTES);
            return skuOrderInfoRespDTO;
        }
        return null;
    }

    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Long orderId) {
        String cacheKey = cacheKeyBuilder.buildSkuOrderInfo(orderId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        if (cacheObj != null) {
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectById(orderId);
        if (skuOrderInfoPO != null) {
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO,60, TimeUnit.MINUTES);
            return skuOrderInfoRespDTO;
        }
        return null;
    }


    @Override
    public SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        String skuIdListStr = StringUtils.join(skuOrderInfoReqDTO.getSkuIdList(),",");
        SkuOrderInfoPO skuOrderInfoPO = ConvertBeanUtils.convert(skuOrderInfoReqDTO, SkuOrderInfoPO.class);
        skuOrderInfoPO.setSkuIdList(skuIdListStr);
        skuOrderInfoMapper.insert(skuOrderInfoPO);
        return skuOrderInfoPO;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO) {
        SkuOrderInfoPO skuOrderInfoPO = new SkuOrderInfoPO();
        skuOrderInfoPO.setStatus(reqDTO.getStatus());
        skuOrderInfoPO.setId(reqDTO.getId());
        skuOrderInfoMapper.updateById(skuOrderInfoPO);
        String cacheKey = cacheKeyBuilder.buildSkuOrder(reqDTO.getUserId(), reqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }
}
