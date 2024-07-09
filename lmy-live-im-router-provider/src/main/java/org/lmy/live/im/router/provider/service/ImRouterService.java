package org.lmy.live.im.router.provider.service;

public interface ImRouterService {
    /**
     * 发送消息
     * @param userId
     * @param msgBody
     * @return
     */
    boolean sendMsg(Long userId,String msgBody);
}
