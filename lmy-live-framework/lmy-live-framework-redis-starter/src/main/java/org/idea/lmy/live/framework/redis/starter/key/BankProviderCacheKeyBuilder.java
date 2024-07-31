package org.idea.lmy.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class BankProviderCacheKeyBuilder extends RedisKeyBuilder {
    private static String BALANCE_CACHE = "balance_cache";

    public String buildGiftListCacheKey(Long userId) {
        return super.getPrefix() + BALANCE_CACHE+super.getSplitItem()+userId;
    }
}
