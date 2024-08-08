package org.lmy.live.gift.provider.service;

import org.lmy.live.gift.provider.dao.po.SkuInfoPO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:04 2023/10/3
 * @Description
 */
public interface ISkuInfoService {

    /**
     * 批量skuId查询
     *
     * @param skuIdList
     * @return
     */
    List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList);

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuId(Long skuId);

    /**
     * 查询商品详情
     *
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuIdFromCache(Long skuId);
}
