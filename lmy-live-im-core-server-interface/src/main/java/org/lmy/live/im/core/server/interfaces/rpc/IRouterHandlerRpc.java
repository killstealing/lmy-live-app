package org.lmy.live.im.core.server.interfaces.rpc;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

import java.util.List;

public interface IRouterHandlerRpc {
    /**
     * 发送消息
     * @param msgBody
     * @return
     */
    boolean sendMsg(ImMsgBodyDTO msgBody);
    /**
     * 批量发送消息 群聊功能
     * @param msgBodyDTOS
     */
    void batchSendMsg(List<ImMsgBodyDTO> msgBodyDTOS);
}

