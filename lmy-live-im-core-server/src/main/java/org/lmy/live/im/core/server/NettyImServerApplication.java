package org.lmy.live.im.core.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.lmy.live.im.core.server.common.ImMsgDecoder;
import org.lmy.live.im.core.server.common.ImMsgEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyImServerApplication {
    private static final Logger logger= LoggerFactory.getLogger(NettyImServerApplication.class);
    //指定监听的端口
    private int port;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void startApplication(int port) throws InterruptedException {
        setPort(port);

        //处理accept 事件
        NioEventLoopGroup bossGroup=new NioEventLoopGroup();
        //处理read&write事件
        NioEventLoopGroup workerGroup=new NioEventLoopGroup();
        ServerBootstrap serverBootstrap=new ServerBootstrap();
        serverBootstrap.group(bossGroup,workerGroup);
        serverBootstrap.channel(NioServerSocketChannel.class);
        //netty初始化相关的handler
        serverBootstrap.childHandler(new ChannelInitializer<>() {
            @Override
            protected void initChannel(Channel channel) throws Exception {
                //打印日志， 方便观察
                logger.info("初始化连接渠道");
                //设计消息体
                //增加编解码器
                channel.pipeline().addLast(new ImMsgDecoder());
                channel.pipeline().addLast(new ImMsgEncoder());
                //设置这个netty处理handler
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        //
        ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
        logger.info("服务启动成功，监听端口为 {}",getPort());
        //这里会阻塞掉主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    public static void main(String[] args) throws InterruptedException {
        NettyImServerApplication nettyImServerApplication=new NettyImServerApplication();
        nettyImServerApplication.startApplication(9090);
    }
}
