package org.idea.lmy.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class BankProviderCacheKeyBuilder extends RedisKeyBuilder {
    private static String BALANCE_CACHE = "balance_cache";
    private static String PAY_PRODUCT_CACHE="pay_product_cache";
    public String buildPayProductCacheKey(Integer type){
        return super.getPrefix()+PAY_PRODUCT_CACHE+super.getSplitItem()+type;
    }

    public String buildGiftListCacheKey(Long userId) {
        return super.getPrefix() + BALANCE_CACHE+super.getSplitItem()+userId;
    }
}
