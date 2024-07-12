package org.lmy.live.msg.provider.consumer.handler;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface MessageHandler {
    void onReceive(ImMsgBodyDTO imMsgBodyDTO);
}
