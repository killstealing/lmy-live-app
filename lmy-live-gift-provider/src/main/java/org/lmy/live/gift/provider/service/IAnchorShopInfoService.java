package org.lmy.live.gift.provider.service;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:05 2023/10/3
 * @Description
 */
public interface IAnchorShopInfoService {

    /**
     * 根据主播id查询skuId信息
     *
     * @param anchorId
     * @return
     */
    List<Long> querySkuIdByAnchorId(Long anchorId);
    /**
     * 查询有效的主播id
     */
    List<Long> queryAllValidAnchorId();
}
