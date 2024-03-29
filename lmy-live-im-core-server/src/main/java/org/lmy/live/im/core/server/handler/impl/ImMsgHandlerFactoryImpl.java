package org.lmy.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.ImMsgHandlerFactory;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;

import java.util.HashMap;
import java.util.Map;

public class ImMsgHandlerFactoryImpl implements ImMsgHandlerFactory {
    private static Map<Integer, SimplyMsgHandler> msgHandlerMap = new HashMap<>();

    static {
        //登录消息包，登录token认证，channel 和userId关联
        //等出消息包，正常断开im连接的时候发送的
        //业务消息包，最常用的消息类型，例如我们的im发送数据，或者接收数据的时候会用到
        //心跳消息包，定时会给im发送，汇报功能
        msgHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), new LoginMsgHandler());
        msgHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), new LogoutMsgHandler());
        msgHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), new BizMsgHandler());
        msgHandlerMap.put(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(), new HeartBeatMsgHandler());
    }

    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
        SimplyMsgHandler simplyMsgHandler = msgHandlerMap.get(imMsg.getCode());
        if (simplyMsgHandler == null) {
            throw new IllegalArgumentException("msg code is error, code is " + imMsg.getCode());
        }
        simplyMsgHandler.msgHanlder(ctx, imMsg);
    }
}
