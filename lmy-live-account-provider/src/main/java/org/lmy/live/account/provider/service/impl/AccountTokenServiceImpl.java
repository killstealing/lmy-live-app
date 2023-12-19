package org.lmy.live.account.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.lmy.live.account.provider.service.IAccountTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountTokenServiceImpl implements IAccountTokenService {

    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private AccountProviderCacheKeyBuilder accountProviderCacheKeyBuilder;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token= UUID.randomUUID().toString();
        String key = accountProviderCacheKeyBuilder.buildAccountTokenKey(token);
        redisTemplate.opsForValue().set(key,userId,30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String key = accountProviderCacheKeyBuilder.buildAccountTokenKey(tokenKey);
        Integer userId = (Integer) redisTemplate.opsForValue().get(key);
        return userId==null?null:Long.valueOf(userId);
    }
}
