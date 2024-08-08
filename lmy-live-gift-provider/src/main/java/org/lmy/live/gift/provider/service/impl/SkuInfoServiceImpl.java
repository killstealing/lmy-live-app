package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.gift.provider.dao.mapper.SkuInfoMapper;
import org.lmy.live.gift.provider.dao.po.SkuInfoPO;
import org.lmy.live.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @Author idea
 * @Date: Created in 20:08 2023/10/3
 * @Description
 */
@Service
public class SkuInfoServiceImpl implements ISkuInfoService {
    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuInfoPO> qw = new LambdaQueryWrapper<>();
        qw.in(SkuInfoPO::getSkuId, skuIdList);
        qw.eq(SkuInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return skuInfoMapper.selectList(qw);
    }

    @Override
    public SkuInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuInfoPO> qw = new LambdaQueryWrapper<>();
        qw.eq(SkuInfoPO::getSkuId, skuId);
        qw.eq(SkuInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        qw.last("limit 1");
        return skuInfoMapper.selectOne(qw);
    }

    @Override
    public SkuInfoPO queryBySkuIdFromCache(Long skuId) {
        String detailKey = cacheKeyBuilder.buildSkuDetail(skuId);
        Object skuInfoCacheObj = redisTemplate.opsForValue().get(detailKey);
        if (skuInfoCacheObj != null) {
            return (SkuInfoPO) skuInfoCacheObj;
        }
        SkuInfoPO skuInfoPO = this.queryBySkuId(skuId);
        if(skuInfoPO == null) {
            return null;
        }
        redisTemplate.opsForValue().set(detailKey,skuInfoPO,1, TimeUnit.DAYS);
        return skuInfoPO;
    }
}
