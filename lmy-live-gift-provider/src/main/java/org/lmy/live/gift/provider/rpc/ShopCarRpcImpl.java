package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.gift.interfaces.dto.ShopCarReqDTO;
import org.lmy.live.gift.interfaces.dto.ShopCarRespDTO;
import org.lmy.live.gift.interfaces.rpc.IShopCarRPC;
import org.lmy.live.gift.provider.service.IShopCarService;

/**
 * @Author idea
 * @Date: Created in 16:26 2023/10/4
 * @Description
 */
@DubboService
public class ShopCarRpcImpl implements IShopCarRPC {

    @Resource
    private IShopCarService shopCarService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.getCarInfo(shopCarReqDTO);
    }

    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.addCar(shopCarReqDTO);
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.removeFromCar(shopCarReqDTO);
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.clearShopCar(shopCarReqDTO);
    }
}
