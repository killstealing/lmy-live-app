package org.lmy.live.im.interfaces.rpc;

public interface ImOnlineRpc {
    /**
     * 判断用户是不是在线
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(Long userId,Integer appId);
}
