package org.lmy.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.ImProviderCacheKeyBuilder;
import org.lmy.live.im.provider.service.ImTokenService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class ImTokenServiceImpl implements ImTokenService {
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createImLoginToken(Long userId, int appId) {
        String token= UUID.randomUUID().toString();
        String key = cacheKeyBuilder.buildImLoginTokenKey(token);
        redisTemplate.opsForValue().set(key,userId,5, TimeUnit.MINUTES);
        return token;
    }

    @Override
    public Long getUserIdByToken(String token) {
        String key = cacheKeyBuilder.buildImLoginTokenKey(token);
        Object userId = redisTemplate.opsForValue().get(key);
        return userId==null?null:Long.valueOf((Integer)userId);
    }
}
