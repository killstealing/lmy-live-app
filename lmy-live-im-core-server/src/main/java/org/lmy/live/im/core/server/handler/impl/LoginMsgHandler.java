package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class LoginMsgHandler implements SimplyMsgHandler {

//    @DubboReference
//    private ImTokenRpc imTokenRpc;

    private static final Logger logger = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
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
//        Long userId = imTokenRpc.getUserIdByToken(token);
        Long userId=1312312L;
        if (userId!=null && userId.equals(imMsgBodyDTO.getUserId())){
            ChannelHandlerContextCache.put(userId,ctx);
            ImContextUtils.setUserId(ctx,userId);
            ImContextUtils.setAppId(ctx,appIdFromMsg);
            ImMsgBodyDTO respBody=new ImMsgBodyDTO();
            respBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
            respBody.setUserId(userId);
            respBody.setData("true");
            ImMsg respMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(),JSON.toJSONString(respBody));
            logger.info("login successfully , userId is {}, appId is {}", userId,appIdFromMsg);
            ctx.writeAndFlush(respMsg);
            return;
        }
        ctx.close();
        logger.error("token check error, imMsg is {}", imMsg);
        throw new IllegalArgumentException("token check error");
    }
}
