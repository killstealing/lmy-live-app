package org.lmy.live.gift.provider.service;

import org.lmy.live.gift.interfaces.dto.GiftRecordDTO;

public interface IGiftRecordService {
    /**
     * 插入单个礼物信息
     *
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
