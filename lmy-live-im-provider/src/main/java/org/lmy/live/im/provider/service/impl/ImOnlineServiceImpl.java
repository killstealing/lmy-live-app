package org.lmy.live.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.lmy.live.im.provider.service.ImOnlineService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean isOnline(Long userId, Integer appId) {
        return stringRedisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
    }
}
