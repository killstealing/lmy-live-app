package org.lmy.live.im.router.provider.service;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface ImRouterService {
    /**
     * 发送消息
     * @param userId
     * @param msgBody
     * @return
     */
    boolean sendMsg(Long userId, ImMsgBodyDTO msgBody);
}
