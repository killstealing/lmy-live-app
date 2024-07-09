package org.lmy.live.im.router.interfaces.rpc;

public interface ImRouterRpc {
    /**
     * 发送消息
     * @param userId
     * @param msgBody
     * @return
     */
    boolean sendMsg(Long userId,String msgBody);
}
