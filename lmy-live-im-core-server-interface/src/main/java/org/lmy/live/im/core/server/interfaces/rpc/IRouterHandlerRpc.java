package org.lmy.live.im.core.server.interfaces.rpc;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface IRouterHandlerRpc {
    /**
     * 发送消息
     * @param msgBody
     * @return
     */
    boolean sendMsg(ImMsgBodyDTO msgBody);
}

