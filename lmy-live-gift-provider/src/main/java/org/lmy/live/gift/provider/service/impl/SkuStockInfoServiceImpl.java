package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.gift.provider.dao.mapper.SkuStockInfoMapper;
import org.lmy.live.gift.provider.dao.po.SkuStockInfoPO;
import org.lmy.live.gift.provider.service.ISkuStockInfoService;
import org.lmy.live.gift.provider.service.bo.DcrStockNumBO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 08:56 2023/10/5
 * @Description
 */
@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {

    @Resource
    private SkuStockInfoMapper skuStockInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    private String LUA_SCRIPT =
            "if (redis.call('exists', KEYS[1])) == 1 then " +
                    " local currentStock=redis.call('get',KEYS[1]) " +
                    "  if (tonumber(currentStock)>0 and tonumber(currentStock)-tonumber(ARGV[1])>=0)  then " +
                    "      return redis.call('decrby',KEYS[1],tonumber(ARGV[1])) " +
                    "   else return -1 end " +
                    "else " +
                    "return -1 end";

    @Override
    public boolean updateStockNum(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = new SkuStockInfoPO();
        skuStockInfoPO.setStockNum(num);
        LambdaUpdateWrapper<SkuStockInfoPO> uw = new LambdaUpdateWrapper<>();
        uw.eq(SkuStockInfoPO::getSkuId,skuId);
        skuStockInfoMapper.update(skuStockInfoPO,uw);
        return true;
    }

    @Override
    public DcrStockNumBO dcrStockNumBySkuId(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = this.queryBySkuId(skuId);
        DcrStockNumBO dcrStockNumBO = new DcrStockNumBO();
        if (skuStockInfoPO.getStockNum() == 0 || skuStockInfoPO.getStockNum() - num < 0) {
            dcrStockNumBO.setNoStock(true);
            dcrStockNumBO.setSuccess(false);
            return dcrStockNumBO;
        }
        dcrStockNumBO.setNoStock(false);
        boolean updateState = skuStockInfoMapper.dcrStockNumBySkuId(skuId, num, skuStockInfoPO.getVersion()) > 0;
        dcrStockNumBO.setSuccess(updateState);
        return dcrStockNumBO;
    }

    @Override
    public boolean decrStockNumBySkuIdV2(Long skuId, Integer num) {
        //直接使用redis命令操作的话，可能会有多元请求，用lua方案去替代进行改良
        //根据skuId查询库存信息，从缓存好的redis中去取库存信息 网络请求
        //判断：sku库存值>0，sku库存值-num>0，（其他线程 也在这么操作）
        //扣减 decrby 网络请求 导致超卖
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        String skuStockCacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        return redisTemplate.execute(redisScript, Collections.singletonList(skuStockCacheKey), num) >= 0;
    }

    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> qw = new LambdaQueryWrapper<>();
        qw.eq(SkuStockInfoPO::getSkuId, skuId);
        qw.eq(SkuStockInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        qw.last("limit 1");
        return skuStockInfoMapper.selectOne(qw);
    }

    @Override
    public List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuStockInfoPO> qw = new LambdaQueryWrapper<>();
        qw.in(SkuStockInfoPO::getSkuId, skuIdList);
        qw.eq(SkuStockInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return skuStockInfoMapper.selectList(qw);
    }
}
