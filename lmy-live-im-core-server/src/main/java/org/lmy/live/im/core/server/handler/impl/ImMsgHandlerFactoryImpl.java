package org.lmy.live.im.core.server.handler.impl;

import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.lmy.live.im.core.server.common.ImMsg;
import org.lmy.live.im.core.server.handler.ImMsgHandlerFactory;
import org.lmy.live.im.core.server.handler.SimplyMsgHandler;
import org.lmy.live.im.interfaces.constants.ImMsgCodeEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ImMsgHandlerFactoryImpl implements ImMsgHandlerFactory, InitializingBean {
    private static Map<Integer, SimplyMsgHandler> msgHandlerMap = new HashMap<>();

    @Resource
    private ApplicationContext applicationContext;
    @Override
    public void msgHanlder(ChannelHandlerContext ctx, ImMsg imMsg) {
        SimplyMsgHandler simplyMsgHandler = msgHandlerMap.get(imMsg.getCode());
        if (simplyMsgHandler == null) {
            throw new IllegalArgumentException("msg code is error, code is " + imMsg.getCode());
        }
        simplyMsgHandler.msgHandler(ctx, imMsg);
    }
    @Override
    public void afterPropertiesSet() throws Exception {
        //登录消息包，登录token认证，channel 和userId关联
        //等出消息包，正常断开im连接的时候发送的
        //业务消息包，最常用的消息类型，例如我们的im发送数据，或者接收数据的时候会用到
        //心跳消息包，定时会给im发送，汇报功能
        msgHandlerMap.put(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), applicationContext.getBean(LoginMsgHandler.class));
        msgHandlerMap.put(ImMsgCodeEnum.IM_LOGOUT_MSG.getCode(), applicationContext.getBean(LogoutMsgHandler.class));
        msgHandlerMap.put(ImMsgCodeEnum.IM_BIZ_MSG.getCode(), applicationContext.getBean(BizMsgHandler.class));
        msgHandlerMap.put(ImMsgCodeEnum.IM_HEART_BEAT_MSG.getCode(), applicationContext.getBean(HeartBeatMsgHandler.class));
    }
}
