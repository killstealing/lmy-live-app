package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.gift.interfaces.dto.GiftRecordDTO;
import org.lmy.live.gift.interfaces.rpc.IGiftRecordRpc;
import org.lmy.live.gift.provider.service.IGiftRecordService;

@DubboService
public class GiftRecordRpcImpl implements IGiftRecordRpc {

    @Resource
    private IGiftRecordService giftRecordService;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        giftRecordService.insertOne(giftRecordDTO);
    }
}
