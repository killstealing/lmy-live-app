package imclient;

import com.alibaba.fastjson.JSON;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.dubbo.config.annotation.DubboReference;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.common.ImMsgDecoder;
import org.lmy.live.im.core.server.common.ImMsgEncoder;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.interfaces.rpc.ImTokenRpc;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImClientHandler implements InitializingBean {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread clientThread=new Thread(() -> {
            EventLoopGroup eventLoopGroup=new NioEventLoopGroup();
            Bootstrap bootstrap=new Bootstrap();
            bootstrap.group(eventLoopGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel channel) throws Exception {
                    System.out.println("初始化连接建立");
                    channel.pipeline().addLast(new ImMsgEncoder());
                    channel.pipeline().addLast(new ImMsgDecoder());
                    channel.pipeline().addLast(new ClientHandler());
                }
            });
            Map<Long, Channel> userIdChannelMap=new HashMap<>();
            //先登录
            for (int i = 0; i < 10; i++) {
                Long userId=200000L+i;
                String imLoginToken = imTokenRpc.createImLoginToken(userId, AppIdEnum.LMY_LIVE_BIZ.getCode());
                ChannelFuture channelFuture=null;
                try {
                    channelFuture = bootstrap.connect("localhost", 8085).sync();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                Channel channel = channelFuture.channel();
                ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
                imMsgBodyDTO.setUserId(userId);
                imMsgBodyDTO.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                imMsgBodyDTO.setToken(imLoginToken);
                imMsgBodyDTO.setData("true");
                ImMsg imMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
                channel.writeAndFlush(imMsg);
                userIdChannelMap.put(userId,channel);
            }
            //发送心跳
            while (true){
                for (Long userId :userIdChannelMap.keySet()) {
                    //
                    ImMsgBodyDTO imMsgBodyDTO=new ImMsgBodyDTO();
                    imMsgBodyDTO.setUserId(userId);
                    imMsgBodyDTO.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                    imMsgBodyDTO.setData("true");
                    ImMsg imMsg=ImMsg.buildMsg(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
                    Channel channel = userIdChannelMap.get(userId);
                    channel.writeAndFlush(imMsg);
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        clientThread.start();
    }
}
