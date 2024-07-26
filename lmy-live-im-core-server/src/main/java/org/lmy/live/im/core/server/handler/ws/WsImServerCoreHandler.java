package org.lmy.live.im.core.server.handler.ws;

import com.alibaba.fastjson2.JSONObject;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ChannelHandlerContextCache;
import org.lmy.live.im.core.server.common.ImContextUtils;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.ImMsgHandlerFactory;
import org.lmy.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@ChannelHandler.Sharable
public class WsImServerCoreHandler extends SimpleChannelInboundHandler {
    private static final Logger logger= LoggerFactory.getLogger(WsImServerCoreHandler.class);

    @Resource
    private ImMsgHandlerFactory imMsgHandlerFactory;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(msg instanceof WebSocketFrame){
            wsMsgHandler(ctx, (WebSocketFrame) msg);
        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Long userId= ImContextUtils.getUserId(ctx);
        Integer appId=ImContextUtils.getAppId(ctx);
        if(userId!=null&&appId!=null){
            ChannelHandlerContextCache.remove(userId);
            //移除用户之前连接上的ip记录
            redisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId);
        }
    }

    private void wsMsgHandler(ChannelHandlerContext ctx, WebSocketFrame msg){
        //如果不是文本消息，统一后台不会处理
        if(!(msg instanceof TextWebSocketFrame)){
            logger.error(String.format("[WsImServerCoreHandler] wsMsgHandler, %s msg types not supported",msg.getClass().getName()));
            return;
        }
        try {
            //返回应答消息
            String content=((TextWebSocketFrame) msg).text();
            JSONObject jsonObject= JSONObject.parseObject(content, JSONObject.class);
            ImMsg imMsg=new ImMsg();
            imMsg.setMagic(jsonObject.getShort("magic"));
            imMsg.setCode(jsonObject.getInteger("code"));
            imMsg.setLen(jsonObject.getInteger("len"));
            imMsg.setBody(jsonObject.getString("body").getBytes());
            imMsgHandlerFactory.msgHanlder(ctx,imMsg);
        }catch (Exception e){
            logger.error("[WsImServerCoreHandler] wsMsgHandler error is:",e);
        }
    }
}
