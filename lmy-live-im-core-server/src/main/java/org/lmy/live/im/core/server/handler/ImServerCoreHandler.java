package org.lmy.live.im.core.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextAttr;
import org.lmy.live.im.core.server.common.ImMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class ImServerCoreHandler extends SimpleChannelInboundHandler {
    private static final Logger logger= LoggerFactory.getLogger(ImServerCoreHandler.class);

    @Resource
    private ImMsgHandlerFactory imMsgHandlerFactory;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object obj) throws Exception {
        if(!(obj instanceof ImMsg)){
            throw new IllegalArgumentException("error msg, msg is "+obj);
        }
        ImMsg imMsg= (ImMsg) obj;
        logger.info("ImServerCoreHandler:channelRead0:imMsgHandlerFactory"+imMsgHandlerFactory);
        imMsgHandlerFactory.msgHanlder(ctx,imMsg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId = ctx.attr(ImContextAttr.USER_ID).get();
        if(userId!=null){
            ChannelHandlerContextCache.remove(userId);
        }
    }
}
