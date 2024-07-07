package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.lmy.live.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BizMsgHandler implements SimplyMsgHandler {
    @Resource
    private MQProducer mqProducer;
    private static final Logger logger= LoggerFactory.getLogger(BizMsgHandler.class);

    @Override
    public void msgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        logger.info("[BizMsgHandler] imMsg is {}",imMsg);
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId==null||appId==null){
            logger.error("BizMsgHandler [msgHandler] userId is null, appId is null, imMsg is {}", imMsg);
            throw new IllegalArgumentException("attr is error");
        }
        byte[] body = imMsg.getBody();
        if(body==null || body.length==0){
            ctx.close();
            logger.error("BizMsgHandler [msgHandler] body error, imMsg is {}", imMsg);
            throw new IllegalArgumentException("body error");
        }
        ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
        imMsgBodyDTO.setUserId(userId);
        imMsgBodyDTO.setAppId(appId);
        imMsgBodyDTO.setData("true");
        ImMsg respMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
        ctx.writeAndFlush(respMsg);
        Message message=new Message();
        message.setTopic(ImCoreServerProviderTopicNames.LMY_LIVE_IM_BIZ_MSG_TOPIC);
        message.setBody(body);
        try {
            SendResult sendResult = mqProducer.send(message);
            logger.info("[BizMsgHandler]消息投递结构：{}",sendResult);
        } catch (Exception e) {
            logger.info("[BizMsgHandler] send message has error：{}",e);
            throw new RuntimeException(e);
        }
    }
}
