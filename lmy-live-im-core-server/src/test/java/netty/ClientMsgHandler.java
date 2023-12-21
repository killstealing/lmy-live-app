package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.lmy.live.im.core.server.common.ImMsg;

public class ClientMsgHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ImMsg imMsg= (ImMsg) msg;
        System.out.println("【服务端相应数据】 result is "+imMsg);
    }
}
