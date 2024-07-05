package org.lmy.live.im.core.server.common;


import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandlerContextCache {
    private static final Map<Long, ChannelHandlerContext> chcc=new HashMap<>();
    public static void put(Long userId, ChannelHandlerContext ctx){
        chcc.put(userId,ctx);
    }
    public static ChannelHandlerContext get(Long userId){
        return chcc.get(userId);
    }
    public static void remove(Long userId){
        chcc.remove(userId);
    }
}
