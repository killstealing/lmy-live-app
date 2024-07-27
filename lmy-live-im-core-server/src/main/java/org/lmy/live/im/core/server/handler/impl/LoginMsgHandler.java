package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.lmy.live.im.core.server.interfaces.dto.ImOnlineDTO;
import org.lmy.live.im.interfaces.constants.ImConstants;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.interfaces.rpc.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.concurrent.TimeUnit;

@Component
public class LoginMsgHandler implements SimplyMsgHandler {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private MQProducer producer;

    private static final Logger logger = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Override
    public void msgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        if(ImContextUtils.getUserId(ctx)!=null){
            logger.error("LoginMsgHandler [msgHanlder] userId is existing, imMsg is {}", imMsg);
            throw new IllegalArgumentException("已经登录了");
        }
        byte[] body = imMsg.getBody();
        if(body==null || body.length==0){
            ctx.close();
            logger.error("LoginMsgHandler [msgHanlder] body error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBodyDTO imMsgBodyDTO = JSON.parseObject(new String(body), ImMsgBodyDTO.class);
        Long userIdFromMsg=imMsgBodyDTO.getUserId();
        Integer appIdFromMsg = imMsgBodyDTO.getAppId();
        String token = imMsgBodyDTO.getToken();
        if (StringUtils.isEmpty(token)||userIdFromMsg<10000||appIdFromMsg<10000){
            ctx.close();
            logger.error("LoginMsgHandler [msgHanlder] token error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("token error");
        }
        Long userId = imTokenRpc.getUserIdByToken(token);
        if (userId!=null && userId.equals(imMsgBodyDTO.getUserId())){
            loginSuccessHandler(ctx,userId,appIdFromMsg,null);
            return;
        }
        ctx.close();
        logger.error("token check error, imMsg is {}", imMsg);
        throw new IllegalArgumentException("token check error");
    }

    /**
     * 如果用户登录成功，则处理相关记录
     * @param ctx
     * @param userId
     * @param appId
     */
    public void loginSuccessHandler(ChannelHandlerContext ctx,Long userId,Integer appId,Integer roomId){
        //按照userId保存好相关的channel对象信息
        ChannelHandlerContextCache.put(userId,ctx);
        ImContextUtils.setUserId(ctx,userId);
        ImContextUtils.setAppId(ctx,appId);
        //将IM消息回写给客户端
        ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
        imMsgBodyDTO.setAppId(appId);
        imMsgBodyDTO.setUserId(userId);
        imMsgBodyDTO.setData("true");
        ImMsg imMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId,
                ChannelHandlerContextCache.getServerIpAddress()+"%"+userId,ImConstants.DEFAULT_HEART_BEAT_GAP*2,TimeUnit.SECONDS);
        logger.info("[LoginMsgHandler] login success,userId is {},appId is {}",userId,appId);
        ctx.writeAndFlush(imMsg);
        sendLoginMQ(userId,appId,roomId);
    }

    private void sendLoginMQ(Long userId,Integer appId,Integer roomId){
        ImOnlineDTO imOnlineDTO=new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        Message message=new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOnlineDTO).getBytes());
        try {
            SendResult sendResult = producer.send(message);
            logger.info("[LoginMsgHandler] sendLoginMQ sendResult is {}",sendResult);
        } catch (Exception e) {
            logger.error("[LoginMsgHandler] sendLoginMQ exception is"+e);
        }
    }
}
