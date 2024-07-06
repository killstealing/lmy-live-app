package org.idea.lmy.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class ImCoreServerProviderCacheKeyBuilder extends RedisKeyBuilder {
    private static final String IM_ONLINE_ZSET = "imOnlineXset";

    public String buildImCoreServerProviderKey(Long userId,Integer appId) {
        return super.getPrefix() + IM_ONLINE_ZSET + super.getSplitItem() + appId+super.getSplitItem()+userId%10000;
    }
}
