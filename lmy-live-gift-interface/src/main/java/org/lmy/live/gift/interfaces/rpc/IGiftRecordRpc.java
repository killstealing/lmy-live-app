package org.lmy.live.gift.interfaces.rpc;

import org.lmy.live.gift.interfaces.dto.GiftRecordDTO;

public interface IGiftRecordRpc {
    /**
     * 插入单个礼物信息
     *
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
