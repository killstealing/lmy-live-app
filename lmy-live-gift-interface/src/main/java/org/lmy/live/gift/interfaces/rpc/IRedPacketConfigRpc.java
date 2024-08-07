package org.lmy.live.gift.interfaces.rpc;

import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigRespDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;

/**
 * @Author idea
 * @Date: Created in 22:57 2023/9/5
 * @Description
 */
public interface IRedPacketConfigRpc {

    /**
     * 按照主播id查询红包雨配置
     *
     * @param anchorId
     * @return
     */
    RedPacketConfigRespDTO queryByAnchorId(Long anchorId);

    /**
     * 新增红包配置
     *
     * @param redPacketConfigReqDTO
     */
    boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO);

    /**
     * 更新红包雨配置
     *
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO);
    //红包怎么生成
    /**
     * 提前生成红包雨的数据
     *
     * @param anchorId
     */
    boolean prepareRedPacket(Long anchorId);
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);
}
