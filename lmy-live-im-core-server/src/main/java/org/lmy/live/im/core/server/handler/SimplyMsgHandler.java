package org.lmy.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ImMsg;

public interface SimplyMsgHandler {
    void msgHandler(ChannelHandlerContext ctx, ImMsg imMsg);
}
