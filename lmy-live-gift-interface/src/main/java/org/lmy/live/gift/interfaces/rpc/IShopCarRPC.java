package org.lmy.live.gift.interfaces.rpc;

import org.lmy.live.gift.interfaces.dto.ShopCarReqDTO;

/**
 * @Author idea
 * @Date: Created in 16:25 2023/10/4
 * @Description 商品购物车接口
 */
public interface IShopCarRPC {

    /**
     * 添加商品到购物车中
     *
     * @param shopCarReqDTO
     */
    Boolean addCar(ShopCarReqDTO shopCarReqDTO);
}
