package org.lmy.live.gift.interfaces.rpc;

import org.lmy.live.gift.interfaces.dto.ShopCarReqDTO;
import org.lmy.live.gift.interfaces.dto.ShopCarRespDTO;

/**
 * @Author idea
 * @Date: Created in 16:25 2023/10/4
 * @Description 商品购物车接口
 */
public interface IShopCarRPC {
    /**
     * 查看购物车信息
     * @param shopCarReqDTO
     */
    ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO);

    /**
     * 添加商品到购物车中
     *
     * @param shopCarReqDTO
     */
    Boolean addCar(ShopCarReqDTO shopCarReqDTO);


    /**
     * 移除购物车
     *
     * @param shopCarReqDTO
     */
    Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 清除整个购物车
     *
     * @param shopCarReqDTO
     */
    Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO);
}
