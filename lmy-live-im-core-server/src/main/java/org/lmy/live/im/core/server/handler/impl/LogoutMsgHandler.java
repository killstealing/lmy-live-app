package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.lmy.live.im.core.server.interfaces.dto.ImOfflineDTO;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgHandler implements SimplyMsgHandler {
    private static final Logger logger= LoggerFactory.getLogger(LogoutMsgHandler.class);
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MQProducer mqProducer;

    @Override
    public void msgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        logger.info("[logoutMsg]:"+imMsg);
        byte[] body = imMsg.getBody();
        if(body==null||body.length==0){
            ctx.close();
            logger.error("LogoutMsgHandler [msgHanlder] body error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        //有可能为空
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId==null||appId==null){
            logger.error("LogoutMsgHandler [msgHanlder] userId empty, appID empty, imMsg is {}", imMsg);
            throw new IllegalArgumentException("userId 为空， 不做处理");
        }
        logoutSuccessfhandler(ctx,userId,appId);
    }

    public void logoutSuccessfhandler(ChannelHandlerContext ctx,Long userId,Integer appId){
        ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
        imMsgBodyDTO.setUserId(userId);
        imMsgBodyDTO.setAppId(appId);
        imMsgBodyDTO.setData("true");
        ImMsg respMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
        ctx.writeAndFlush(respMsg);
        logger.info("LogoutMsgHandler [msgHanlder] response imMsg is {}", respMsg);
        //理想情况下，客户端断线的时候，会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        ImContextUtils.remove(ctx);
        ctx.close();
        sendLogoutMQ(userId,appId);
    }

    private void sendLogoutMQ(Long userId,Integer appId){
        ImOfflineDTO imOfflineDTO=new ImOfflineDTO();
        imOfflineDTO.setUserId(userId);
        imOfflineDTO.setAppId(appId);
//        imOfflineDTO.setRoomId(roomId);
        imOfflineDTO.setLoginTime(System.currentTimeMillis());
        Message message=new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOfflineDTO).getBytes());
        try {
            SendResult sendResult = mqProducer.send(message);
            logger.info("[LogoutMsgHandler] sendLogoutMQ sendResult is {}",sendResult);
        } catch (Exception e) {
            logger.error("[LogoutMsgHandler] sendLogoutMQ exception is"+e);
        }
    }
}
