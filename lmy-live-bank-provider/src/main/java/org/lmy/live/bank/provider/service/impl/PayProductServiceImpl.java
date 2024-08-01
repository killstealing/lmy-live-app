package org.lmy.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.provider.dao.mapper.PayProductMapper;
import org.lmy.live.bank.provider.dao.po.PayProductPO;
import org.lmy.live.bank.provider.service.IPayProductService;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class PayProductServiceImpl implements IPayProductService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private PayProductMapper payProductMapper;

    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<PayProductDTO> getProductList(Integer type) {
        String cacheKey = cacheKeyBuilder.buildPayProductCacheKey(type);
        List<PayProductDTO> cacheList=redisTemplate.opsForList().range(cacheKey,0,30).stream()
                .map(x->{return (PayProductDTO)x;}).collect(Collectors.toList());
        if(!CollectionUtils.isEmpty(cacheList)){
            if(cacheList.get(0).getId()==null){
                return Collections.emptyList();
            }else{
                return cacheList;
            }
        }
        LambdaQueryWrapper<PayProductPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(PayProductPO::getType,type);
        queryWrapper.eq(PayProductPO::getValidStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.orderByDesc(PayProductPO::getPrice);
        List<PayProductPO> payProductPOS = payProductMapper.selectList(queryWrapper);
        List<PayProductDTO> productDbResult = ConvertBeanUtils.convertList(payProductPOS, PayProductDTO.class);
        if(CollectionUtils.isEmpty(productDbResult)){
            redisTemplate.opsForList().leftPush(cacheKey,new PayProductDTO());
            redisTemplate.expire(cacheKey,3, TimeUnit.MINUTES);
        }else{
            redisTemplate.opsForList().leftPushAll(cacheKey,productDbResult.toArray());
            redisTemplate.expire(cacheKey,30,TimeUnit.MINUTES);
        }
        return productDbResult;
    }
}
