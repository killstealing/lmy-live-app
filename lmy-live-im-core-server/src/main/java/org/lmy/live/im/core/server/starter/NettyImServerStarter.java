package org.lmy.live.im.core.server.starter;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImMsgDecoder;
import org.lmy.live.im.core.server.common.ImMsgEncoder;
import org.lmy.live.im.core.server.handler.ImServerCoreHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;


@Configuration
public class NettyImServerStarter implements InitializingBean {
    private static final Logger logger= LoggerFactory.getLogger(NettyImServerStarter.class);

    @Value("${lmy.im.port}")
    private int port;

    @Resource
    private ApplicationContext applicationContext;

    @Resource
    private Environment environment;


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

        // JVM 参数
        // - DUBBO_IP_TO_REGISTRY=192.168.3.26 DUBBO_PORT_TO_REGISTRY=9099
        //获取im服务的注册ip和暴露端口
        String registryIp=environment.getProperty("DUBBO_IP_TO_REGISTRY");
        String registryPort=environment.getProperty("DUBBO_PORT_TO_REGISTRY");
        if(StringUtils.isEmpty(registryIp)||StringUtils.isEmpty(registryPort)){
            throw new IllegalArgumentException("启动参数中的注册端口和注册IP不能为空");
        }
        ChannelHandlerContextCache.setServerIpAddress(registryIp+":"+registryPort);
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
