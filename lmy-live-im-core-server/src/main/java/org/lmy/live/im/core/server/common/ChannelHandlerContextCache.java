package org.lmy.live.im.core.server.common;


import io.netty.channel.ChannelHandlerContext;

import java.util.HashMap;
import java.util.Map;

public class ChannelHandlerContextCache {
    /**
     * 当前IM服务启动的时候，对外暴露的IP和端口
     */
    private static String SERVER_IP_ADDRESS="";
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

    public static String getServerIpAddress() {
        return SERVER_IP_ADDRESS;
    }

    public static void setServerIpAddress(String serverIpAddress) {
        SERVER_IP_ADDRESS = serverIpAddress;
    }
}
