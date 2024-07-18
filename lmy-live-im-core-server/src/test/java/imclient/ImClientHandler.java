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
import org.lmy.live.msg.dto.MessageDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Service;

import java.util.Scanner;

@Service
public class ImClientHandler implements InitializingBean {
    private static final Logger logger = LoggerFactory.getLogger(ImClientHandler.class);

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Override
    public void afterPropertiesSet() throws Exception {
        Thread clientThread = new Thread(() -> {
            EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
            Bootstrap bootstrap = new Bootstrap();
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
            logger.info("请输入userId");
            Scanner scanner = new Scanner(System.in);
            long userId = scanner.nextLong();
            logger.info("请输入objectId");
            long objectId = scanner.nextLong();
            String imLoginToken = imTokenRpc.createImLoginToken(userId, AppIdEnum.LMY_LIVE_BIZ.getCode());
            ChannelFuture channelFuture = null;
            try {
                channelFuture = bootstrap.connect("localhost", 8085).sync();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            Channel channel = channelFuture.channel();
            {
                //先登录
                ImMsgBodyDTO imMsgBodyDTO = new ImMsgBodyDTO();
                imMsgBodyDTO.setUserId(userId);
                imMsgBodyDTO.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                imMsgBodyDTO.setToken(imLoginToken);
                imMsgBodyDTO.setData("true");
                ImMsg imMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO));
                channel.writeAndFlush(imMsg);
                //心跳包机制
                sendHeartBeat(userId, channel);
            }

            //开始聊天
//            {
//                //客户端连接
//                ImMsgBodyDTO imMsgBodyDTO1 = new ImMsgBodyDTO();
//                imMsgBodyDTO1.setUserId(userId);
//                imMsgBodyDTO1.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
//                MessageDTO messageDTO=new MessageDTO();
//                messageDTO.setUserId(userId);
//                messageDTO.setObjectId(objectId);
//                messageDTO.setContent("开始聊天");
//                imMsgBodyDTO1.setData(JSON.toJSONString(messageDTO));
//                ImMsg bizMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO1));
//                channel.writeAndFlush(bizMsg);
//            }
            {
                while (true){
                    System.out.println("请输入消息");
                    String content = scanner.nextLine();
                    //开始聊天
                    ImMsgBodyDTO imMsgBodyDTO1 = new ImMsgBodyDTO();
                    imMsgBodyDTO1.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                    imMsgBodyDTO1.setUserId(userId);
                    imMsgBodyDTO1.setBizCode(5555);
                    MessageDTO messageDTO=new MessageDTO();
                    messageDTO.setUserId(userId);
                    messageDTO.setObjectId(objectId);
                    messageDTO.setContent(content);
                    imMsgBodyDTO1.setData(JSON.toJSONString(messageDTO));
                    ImMsg bizMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), JSON.toJSONString(imMsgBodyDTO1));
                    channel.writeAndFlush(bizMsg);
                }
            }
        });
        clientThread.start();
    }

    private void sendHeartBeat(Long userId,Channel channel){
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(30000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                ImMsgBodyDTO imMsgBody = new ImMsgBodyDTO();
                imMsgBody.setAppId(AppIdEnum.LMY_LIVE_BIZ.getCode());
                imMsgBody.setUserId(userId);
                ImMsg loginMsg = ImMsg.buildMsg(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(), JSON.toJSONString(imMsgBody));
                channel.writeAndFlush(loginMsg);
            }
        }).start();
    }
}
