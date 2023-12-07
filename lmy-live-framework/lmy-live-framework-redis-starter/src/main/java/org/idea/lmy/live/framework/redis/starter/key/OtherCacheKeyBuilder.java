package org.idea.lmy.live.framework.redis.starter.key;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.annotation.Conditional;

/**
 * @Author idea
 * @Date: Created in 20:31 2023/5/14
 * @Description
 */
@Configurable
@Conditional(RedisKeyLoadMatch.class)
public class OtherCacheKeyBuilder extends RedisKeyBuilder {
    private static String USER_INFO_KEY = "other";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY +
                super.getSplitItem() + userId;
    }
}
