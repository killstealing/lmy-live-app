package org.lmy.live.im.core.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.impl.ImMsgHandlerFactoryImpl;

public class ImServerCoreHandler extends SimpleChannelInboundHandler {

    private ImMsgHandlerFactory imMsgHandlerFactory=new ImMsgHandlerFactoryImpl();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        if(!(obj instanceof ImMsg)){
            throw new IllegalArgumentException("error msg, msg is "+obj);
        }
        ImMsg imMsg= (ImMsg) obj;
        imMsgHandlerFactory.msgHanlder(ctx,imMsg);
    }
}
