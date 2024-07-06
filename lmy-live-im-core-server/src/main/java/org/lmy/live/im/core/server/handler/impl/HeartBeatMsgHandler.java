package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.ImConstants;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class HeartBeatMsgHandler implements SimplyMsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatMsgHandler.class);

    @Resource
    private ImCoreServerProviderCacheKeyBuilder imCoreServerProviderCacheKeyBuilder;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
        logger.info("[heartBeatMsg]:"+imMsg);
//        ctx.writeAndFlush(imMsg);
        byte[] body = imMsg.getBody();
        if(body==null||body.length==0){
            ctx.close();
            logger.error("HeartBeatMsgHandler [msgHanlder] body error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId==null||appId==null){
            logger.error("HeartBeatMsgHandler [msgHanlder] userId empty, appID empty, imMsg is {}", imMsg);
            throw new IllegalArgumentException("userId 为空， 不做处理");
        }

        //心跳包基本校验
        //心跳包record记录，redis储存心跳记录
        //zset集合存储心跳记录，基于user Id 去做取模，key(userId)-score(心跳时间)
        String redisKey = imCoreServerProviderCacheKeyBuilder.buildImCoreServerProviderKey(userId, appId);
        //记录用户最近一次心跳时间到zSet上
        this.recordOnlineTime(userId, redisKey);
//        redisTemplate.opsForZSet().add(redisKey,userId,System.currentTimeMillis());
        //清理掉过期不在线的用户留下的心跳记录(在两次心跳包的发送间隔中，如果没有重新更新score值，就会导致被删除)
        this.removeExpireRecord(redisKey);
//        redisTemplate.opsForZSet().removeRangeByScore(redisKey,0,System.currentTimeMillis()- ImConstants.DEFAULT_HEART_BEAT_GAP*1000*2);
        redisTemplate.expire(redisKey,5, TimeUnit.MINUTES);
        ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
        imMsgBodyDTO.setUserId(userId);
        imMsgBodyDTO.setAppId(appId);
        imMsgBodyDTO.setData("true");
        ImMsg respMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
        ctx.writeAndFlush(respMsg);
    }

    /**
     * 清理掉过期不在线的用户留下的心跳记录(在两次心跳包的发送间隔中，如果没有重新更新score值，就会导致被删除)
     *
     * @param redisKey
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

    /**
     * 记录用户最近一次心跳时间到zSet上
     *
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }
}
