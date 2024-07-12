package org.lmy.live.im.router.interfaces.rpc;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface ImRouterRpc {
    /**
     * 发送消息
     * @param userId
     * @param msgBody
     * @return
     */
    boolean sendMsg(Long userId, ImMsgBodyDTO msgBody);
}
