package org.lmy.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogoutMsgHandler implements SimplyMsgHandler {
    private static final Logger logger= LoggerFactory.getLogger(LogoutMsgHandler.class);

    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
        logger.info("logout msg handler start");
    }
}
