package org.lmy.live.living.provider.config;

import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.lmy.live.living.interfaces.constants.LivingRoomTypeEnum;
import org.lmy.live.living.interfaces.dto.LivingRoomRespDTO;
import org.lmy.live.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class RefreshLivingRoomListJob implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(RefreshLivingRoomListJob.class);
    private ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);
    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private LivingProviderCacheKeyBuilder livingProviderCacheKeyBuilder;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public void afterPropertiesSet() throws Exception {
        schedulePool.scheduleWithFixedDelay(new RefreshCacheListJob(),3000,1000, TimeUnit.MILLISECONDS);
    }
    class RefreshCacheListJob implements Runnable {

        @Override
        public void run() {
            String cacheKey = livingProviderCacheKeyBuilder.buildRefreshLivingRoomListLock();
            //这把锁等它自动过期
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 1, TimeUnit.SECONDS);
            if (lockStatus) {
                logger.info("[RefreshLivingRoomListJob] starting 加载db中记录的直播间进redis里");
                refreshDBToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDBToRedis(LivingRoomTypeEnum.PK_LIVING_ROOM.getCode());
                logger.info("[RefreshLivingRoomListJob] end 加载db中记录的直播间进redis里");
            }
        }
    }
    private void refreshDBToRedis(Integer type){
        String cacheKey = livingProviderCacheKeyBuilder.buildLivingRoomList(type);
        List<LivingRoomRespDTO> resultList = livingRoomService.listAllLivingRoomFromDB(type);
        if(CollectionUtils.isEmpty(resultList)){
            redisTemplate.delete(cacheKey);
            return;
        }
        String tempCacheKey=cacheKey+"_temp";
        //按照查询出来的顺序，一个个地塞入到list集合中
        for (LivingRoomRespDTO livingRoomRespDTO : resultList) {
            redisTemplate.opsForList().rightPush(tempCacheKey, livingRoomRespDTO);
        }
        //正在访问的list集合，del -> leftPush
        //直接修改重命名这个list，不要直接对原先的list进行修改，减少阻塞影响 cow
        redisTemplate.rename(tempCacheKey, cacheKey);
        redisTemplate.delete(tempCacheKey);
    }
}
