package org.lmy.live.im.core.server.handler.ws;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.websocketx.CloseWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshaker;
import io.netty.handler.codec.http.websocketx.WebSocketServerHandshakerFactory;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.lmy.live.im.core.server.handler.impl.LoginMsgHandler;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.rpc.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WsSharkHandler extends ChannelInboundHandlerAdapter {
    private static final Logger logger= LoggerFactory.getLogger(WsSharkHandler.class);

    @Value("${lmy.im.ws.port}")
    private int port;

    @Value("${lmy.im.ws.ip}")
//    @Value("${spring.cloud.nacos.discovery.ip}")
    private String serverIp;

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private LoginMsgHandler loginMsgHandler;

    private WebSocketServerHandshaker webSocketServerHandshaker;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        //握手接入MS
        if(msg instanceof FullHttpRequest){
            handleHttpRequest(ctx, (FullHttpRequest) msg);
            return;
        }
        //正常关闭链路
        if(msg instanceof CloseWebSocketFrame){
            webSocketServerHandshaker.close(ctx.channel(),((CloseWebSocketFrame) msg).retain());
            return;
        }
        //将消息传递给下一个链路处理器去处理
        ctx.fireChannelRead(msg);
    }

    private void handleHttpRequest(ChannelHandlerContext ctx,FullHttpRequest msg){
        String webSockerUrl="ws://"+serverIp+":"+port;
        //构造握手响应返回
        WebSocketServerHandshakerFactory wsFactory=new WebSocketServerHandshakerFactory(webSockerUrl,null,false);
        String uri=msg.uri();
        String token=uri.substring(uri.indexOf("token"),uri.indexOf("&")).replaceAll("token=","");
        Long userId=Long.valueOf(uri.substring(uri.indexOf("userId")).replaceAll("userId=",""));
        Long queryUserId=imTokenRpc.getUserIdByToken(token);
//        Integer appId = Integer.parseInt(token.substring(token.lastIndexOf("%") + 1));
        //token的尾部就是appId
        if(queryUserId==null||!queryUserId.equals(userId)){
            logger.error("[WsSharkHandler] token 校验不通过!");
            //校验不通过，不允许建立连接
            ctx.close();
            return;
        }
        //建立ws的握手链接
        webSocketServerHandshaker=wsFactory.newHandshaker(msg);
        if(webSocketServerHandshaker==null){
            WebSocketServerHandshakerFactory.sendUnsupportedVersionResponse(ctx.channel());
            return;
        }
        ChannelFuture channelFuture = webSocketServerHandshaker.handshake(ctx.channel(), msg);
        //首次握手建立ws连接后，返回一定的内容给到客户端
        if (channelFuture.isSuccess()){
            loginMsgHandler.loginSuccessHandler(ctx,userId, AppIdEnum.LMY_LIVE_BIZ.getCode());
            logger.info("[WsSharkHandler] channel is connect!");
        }
    }
}
