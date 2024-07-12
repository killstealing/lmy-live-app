package org.lmy.live.im.router.interfaces.rpc;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface ImRouterRpc {
    /**
     * 发送消息
     * @param msgBody
     * @return
     */
    boolean sendMsg(ImMsgBodyDTO msgBody);
}
