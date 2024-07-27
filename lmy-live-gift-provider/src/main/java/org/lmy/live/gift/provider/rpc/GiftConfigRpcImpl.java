package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.gift.interfaces.dto.GiftConfigDTO;
import org.lmy.live.gift.interfaces.rpc.IGiftConfigRpc;
import org.lmy.live.gift.provider.service.IGiftConfigService;

import java.util.List;

@DubboService
public class GiftConfigRpcImpl implements IGiftConfigRpc {

    @Resource
    private IGiftConfigService giftConfigService;

    @Override
    public GiftConfigDTO getByGiftId(Integer giftId) {
        return giftConfigService.getByGiftId(giftId);
    }

    @Override
    public List<GiftConfigDTO> queryGiftList() {
        return giftConfigService.queryGiftList();
    }

    @Override
    public void insertOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.insertOne(giftConfigDTO);
    }

    @Override
    public void updateOne(GiftConfigDTO giftConfigDTO) {
        giftConfigService.updateOne(giftConfigDTO);
    }
}
