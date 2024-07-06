package org.lmy.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HeartBeatMsgHandler implements SimplyMsgHandler {
    private static final Logger logger = LoggerFactory.getLogger(HeartBeatMsgHandler.class);

    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
        logger.info("[heartBeatMsg]:"+imMsg);
        ctx.writeAndFlush(imMsg);

        //心跳包基本校验
        //心跳包record记录，redis储存心跳记录
        //zset集合存储心跳记录，基于user Id 去做取模，key(userId)-score(心跳时间)

    }
}
