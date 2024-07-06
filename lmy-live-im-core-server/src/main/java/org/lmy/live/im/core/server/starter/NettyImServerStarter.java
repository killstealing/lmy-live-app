package org.lmy.live.im.core.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ImMsgDecoder;
import org.lmy.live.im.core.server.common.ImMsgEncoder;
import org.lmy.live.im.core.server.handler.ImServerCoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;


@Configuration
public class NettyImServerStarter implements InitializingBean {
    private static final Logger logger= LoggerFactory.getLogger(NettyImServerStarter.class);

    @Value("${lmy.im.port}")
    private int port;

    @Resource
    private ApplicationContext applicationContext;


    public void startApplication() throws InterruptedException {
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
                channel.pipeline().addLast(applicationContext.getBean(ImServerCoreHandler.class));
            }
        });
        //基于JVM的钩子函数去实现优雅关闭
        Runtime.getRuntime().addShutdownHook(new Thread(()->{
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }));

        //
        ChannelFuture channelFuture=serverBootstrap.bind(port).sync();
        logger.info("服务启动成功，监听端口为 {}",port);
        //这里会阻塞掉主线程，实现服务长期开启的效果
        channelFuture.channel().closeFuture().sync();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread thread=new Thread(()->{
            try {
                startApplication();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        thread.setName("lmy-live-im-server");
        thread.start();
    }

//    public static void main(String[] args) throws InterruptedException {
//        NettyImServerStarter nettyImServerStarter=new NettyImServerStarter();
//        nettyImServerStarter.startApplication(9090);
//    }
}
