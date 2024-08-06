package org.idea.lmy.live.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author idea
 * @Date: Created in 10:23 2023/6/20
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class GiftProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String GIFT_CONFIG_CACHE = "gift_config_cache";
    private static String GIFT_LIST_CACHE = "gift_list_cache";
    private static String GIFT_CONSUME_KEY = "gift_consume_key";
    private static String GIFT_LIST_LOCK = "gift_list_lock";
    private static String PK_NUM_KEY="pk_num_key";
    private static String PK_NUM_SEQ_KEY="pk_num_seq_key";
    private static String LIVING_PK_IS_OVER = "living_pk_is over";

    public String buildLivingPkIsOver(Integer roomId) {
        return super.getPrefix() + LIVING_PK_IS_OVER + super.getSplitItem() + roomId;
    }
    public String buildPKNumSeqCacheKey(Integer roomId){
        return super.getPrefix()+PK_NUM_SEQ_KEY+super.getSplitItem()+roomId;
    }

    public String buildPKNumCacheKey(Integer roomId){
        return super.getPrefix()+PK_NUM_KEY+super.getSplitItem()+roomId;
    }

    public String buildGiftConsumeCacheKey(String uuid){
        return super.getPrefix()+GIFT_CONSUME_KEY+super.getSplitItem()+uuid;
    }

    public String buildGiftConfigCacheKey(int giftId) {
        return super.getPrefix() + GIFT_CONFIG_CACHE + super.getSplitItem() + giftId;
    }

    public String buildGiftListCacheKey() {
        return super.getPrefix() + GIFT_LIST_CACHE;
    }
    public String buildGiftListLockCacheKey() {
        return super.getPrefix() + GIFT_LIST_LOCK;
    }

}
