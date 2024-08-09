package org.lmy.live.gift.provider.service;


import org.lmy.live.gift.interfaces.dto.SkuOrderInfoReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoRespDTO;

/**
 * @Author idea
 * @Date: Created in 07:12 2023/10/16
 * @Description
 */
public interface ISkuOrderInfoService {

    /**
     * 支持多直播间内用户下单的订单查询
     *
     * @param userId
     * @param roomId
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 插入一条订单信息
     *
     * @param skuOrderInfoReqDTO
     */
    boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 根据订单id修改状态
     *
     * @param skuOrderInfoReqDTO
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);
}
