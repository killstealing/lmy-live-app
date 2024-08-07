package org.lmy.live.gift.provider.service;

import org.lmy.live.gift.interfaces.dto.RedPacketConfigRespDTO;

public interface IRedPacketConfigService {

    /**
     * 支持根据主播id查询是否有红包雨配置特权
     *
     * @param anchorId
     * @return
     */
    RedPacketConfigRespDTO queryByAnchorId(Long anchorId);
}
