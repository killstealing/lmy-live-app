package org.lmy.live.im.core.server.service;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface IRouterHandlerService {
    /**
     * 当收到业务服务的请求 进行处理
     */
    void onReceive(ImMsgBodyDTO imMsgBodyDTO);

    boolean sendMessageToClient(ImMsgBodyDTO imMsgBodyDTO);
}
