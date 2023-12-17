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
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder {
    private static final String USER_INFO_KEY = "userInfo";
    private static final String USER_TAG_KEY = "userTag";

    private static final String USER_LOGIN_TOKEN_KEY = "userLoginToken";
    private static final String USER_PHONE_OBJ_KEY="userPhoneObj";
    private static String USER_PHONE_LIST_KEY = "userPhoneList";

    public String buildUserInfoKey(Long userId) {
        return super.getPrefix() + USER_INFO_KEY +
                super.getSplitItem() + userId;
    }

    public String buildUserTagKey(Long userId) {
        return super.getPrefix() + USER_TAG_KEY +
                super.getSplitItem() + userId;
    }

    public String buildUserLoginTokenKey(String token){
        return super.getPrefix() + USER_TAG_KEY + super.getSplitItem() + token;
    }

    public String buildUserPhoneObjKey(String phone){
        return super.getPrefix() + USER_PHONE_OBJ_KEY + super.getSplitItem() + phone;
    }

    public String buildUserPhoneListKey(Long userId) {
        return super.getPrefix() + USER_PHONE_LIST_KEY + super.getSplitItem() + userId;
    }
}
