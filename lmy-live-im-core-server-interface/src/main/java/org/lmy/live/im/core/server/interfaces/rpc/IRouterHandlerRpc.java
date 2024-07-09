package org.lmy.live.im.core.server.interfaces.rpc;

public interface IRouterHandlerRpc {
    /**
     * 发送消息
     * @param userId
     * @param msgBody
     * @return
     */
    boolean sendMsg(Long userId,String msgBody);
}

