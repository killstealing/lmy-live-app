package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoRespDTO;
import org.lmy.live.gift.interfaces.rpc.ISkuOrderInfoRPC;
import org.lmy.live.gift.provider.service.ISkuOrderInfoService;

/**
 * @Author idea
 * @Date: Created in 07:11 2023/10/16
 * @Description
 */
@DubboService
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId,roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.insertOne(skuOrderInfoReqDTO);
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
    }
}
