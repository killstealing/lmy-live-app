package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class LogoutMsgHandler implements SimplyMsgHandler {
    private static final Logger logger= LoggerFactory.getLogger(LogoutMsgHandler.class);

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
        ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
        imMsgBodyDTO.setUserId(userId);
        imMsgBodyDTO.setAppId(appId);
        imMsgBodyDTO.setData("true");
        ImMsg respMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
        ctx.writeAndFlush(respMsg);
        logger.info("LogoutMsgHandler [msgHanlder] response imMsg is {}", respMsg);
        //理想情况下，客户端断线的时候，会发送一个断线消息包
        ChannelHandlerContextCache.remove(userId);
        ImContextUtils.remove(ctx);
        ctx.close();
    }
}
