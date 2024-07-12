package org.lmy.live.im.router.provider.service;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface ImRouterService {
    /**
     * 发送消息
     * @param msgBody
     * @return
     */
    boolean sendMsg(ImMsgBodyDTO msgBody);
}
