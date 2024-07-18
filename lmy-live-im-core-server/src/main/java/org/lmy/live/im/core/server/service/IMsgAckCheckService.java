package org.lmy.live.im.core.server.service;

import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;

public interface IMsgAckCheckService {

    /**
     *主要是客户端发送ack包到服务端后，调用进行ack记录的清除
     * @param imMsgBodyDTO
     */
    void doMsgAck(ImMsgBodyDTO imMsgBodyDTO);

    /**
     *记录下消息包的ack和times
     * @param imMsgBodyDTO
     */
    void recordMsgAck(ImMsgBodyDTO imMsgBodyDTO,int times);

    /**
     *发送延迟消息，用于进行消息重试功能
     * @param imMsgBodyDTO
     */
    void sendDelayMsg(ImMsgBodyDTO imMsgBodyDTO);

    /**
     *获取ack消息的重试次数
     * @param msgId
     * @param userId
     * @param appId
     * @return
     */
    int getMsgAckTimes(String msgId,Long userId,Integer appId);

}
