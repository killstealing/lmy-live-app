package org.lmy.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ImMsg;

public interface SimplyMsgHandler {
    void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg);
}
