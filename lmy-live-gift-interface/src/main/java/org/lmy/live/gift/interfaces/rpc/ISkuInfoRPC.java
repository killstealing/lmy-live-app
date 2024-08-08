package org.lmy.live.gift.interfaces.rpc;

import org.lmy.live.gift.interfaces.dto.SkuInfoDTO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:09 2023/10/3
 * @Description
 */
public interface ISkuInfoRPC {

    /**
     * 根据主播id查询商品信息
     *
     * @param anchorId
     * @return
     */
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);
}
