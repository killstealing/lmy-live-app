package org.lmy.live.gift.provider.service;

import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;
import org.lmy.live.gift.interfaces.dto.RedPacketReceiveDTO;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;

public interface IRedPacketConfigService {

    /**
     * 支持根据主播id查询是否有红包雨配置特权
     *
     * @param anchorId
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);

    /**
     * 新增红包配置
     *
     * @param redPacketConfigPO
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新红包雨配置
     *
     * @param redPacketConfigPO
     * @return
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    //红包怎么生成
    /**
     * 提前生成红包雨的数据
     *
     * @param anchorId
     */
    boolean prepareRedPacket(Long anchorId);

    //红包怎么领取
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 广播直播间用户，开始抢红包
     *
     * @param reqDTO
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);
    /**
     * 根据红包雨配置code检索信息
     *
     * @param configCode
     */
    RedPacketConfigPO queryByConfigCode(String configCode);
}
