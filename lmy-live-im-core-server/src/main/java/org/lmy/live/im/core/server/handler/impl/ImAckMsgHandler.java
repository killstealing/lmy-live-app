package org.lmy.live.im.core.server.handler.impl;

import com.alibaba.fastjson2.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.core.server.service.IMsgAckCheckService;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class ImAckMsgHandler implements SimplyMsgHandler {
    private static final Logger logger= LoggerFactory.getLogger(ImAckMsgHandler.class);

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private IMsgAckCheckService iMsgAckCheckService;

    @Override
    public void msgHandler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId==null&&appId==null){
            ctx.close();
            throw new IllegalArgumentException("userId and appId is empty");
        }
        ImMsgBodyDTO imMsgBodyDTO = JSON.parseObject(new String(imMsg.getBody()), ImMsgBodyDTO.class);
        logger.info("[ImAckMsgHandler] imMsg is {}",imMsgBodyDTO);
        iMsgAckCheckService.doMsgAck(imMsgBodyDTO);
    }
}
