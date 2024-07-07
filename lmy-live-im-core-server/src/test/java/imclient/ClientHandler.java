package imclient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lmy.live.im.core.server.common.ImMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger= LoggerFactory.getLogger(ClientHandler.class);
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg= (ImMsg) msg;
        logger.info("【服务端相应数据】 imMsg is {}",imMsg);
    }
}
