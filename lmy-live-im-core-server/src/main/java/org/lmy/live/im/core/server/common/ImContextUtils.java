package org.lmy.live.im.core.server.common;

import io.netty.channel.ChannelHandlerContext;

public class ImContextUtils {
    public static Long getUserId(ChannelHandlerContext ctx){
        return ctx.attr(ImContextAttr.USER_ID).get();
    }
    public static void setUserId(ChannelHandlerContext ctx,Long userId){
        ctx.attr(ImContextAttr.USER_ID).set(userId);
    }
    public static Integer getAppId(ChannelHandlerContext ctx){
        return ctx.attr(ImContextAttr.APP_ID).get();
    }
    public static void setAppId(ChannelHandlerContext ctx,Integer appId){
        ctx.attr(ImContextAttr.APP_ID).set(appId);
    }
    public static void remove(ChannelHandlerContext ctx){
        ctx.attr(ImContextAttr.USER_ID).remove();
        ctx.attr(ImContextAttr.APP_ID).remove();
    }
}
