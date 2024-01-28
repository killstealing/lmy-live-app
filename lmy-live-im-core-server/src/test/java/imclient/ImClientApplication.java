package imclient;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.common.ImMsgDecoder;
import org.lmy.live.im.core.server.common.ImMsgEncoder;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImClientApplication {
    private static final Logger logger= LoggerFactory.getLogger(ImClientApplication.class);

    private void startConnection(String address,int port) throws InterruptedException {
        EventLoopGroup clientGroup=new NioEventLoopGroup();
        Bootstrap bootstrap=new Bootstrap();
        bootstrap.group(clientGroup);
        bootstrap.channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            protected void initChannel(SocketChannel channel) throws Exception {
                logger.info("初始化连接建立");
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                channel.pipeline().addLast(new ClientHandler());
            }
        });
        ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
        Channel channel = channelFuture.channel();
        for (int i = 0; i < 100; i++) {
            channel.writeAndFlush(ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(),"login"));
            channel.writeAndFlush(ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(),"logout"));
            channel.writeAndFlush(ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(),"biz"));
            channel.writeAndFlush(ImMsg.buildMsg(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(),"heart"));
            Thread.sleep(3000);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        ImClientApplication clientApplication=new ImClientApplication();
        clientApplication.startConnection("localhost",9090);
    }
}
