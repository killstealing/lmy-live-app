package org.lmy.live.gift.interfaces.rpc;


import org.lmy.live.gift.interfaces.dto.PrepareOrderReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoReqDTO;
import org.lmy.live.gift.interfaces.dto.SkuOrderInfoRespDTO;
import org.lmy.live.gift.interfaces.dto.SkuPrepareOrderInfoDTO;

/**
 * @Author idea
 * @Date: Created in 07:09 2023/10/16
 * @Description
 */
public interface ISkuOrderInfoRPC {

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
     * @param reqDTO
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO reqDTO);

    /**
     * 预支付订单生成
     *
     * @param prepareOrderReqDTO
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO);
}
