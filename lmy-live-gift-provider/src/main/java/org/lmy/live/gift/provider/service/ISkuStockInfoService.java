package org.lmy.live.gift.provider.service;

import org.lmy.live.gift.provider.dao.po.SkuStockInfoPO;
import org.lmy.live.gift.provider.service.bo.DcrStockNumBO;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 08:55 2023/10/5
 * @Description
 */
public interface ISkuStockInfoService {
    /**
     * 更新库存
     * @param skuId
     * @param num
     */
    boolean updateStockNum(Long skuId,Integer num);

    /**
     * 根据skuId更新库存值
     * @param skuId
     * @param num
     */
    DcrStockNumBO dcrStockNumBySkuId(Long skuId, Integer num);

    /**
     * 根据skuId查询库存信息
     *
     * @param skuId
     */
    SkuStockInfoPO queryBySkuId(Long skuId);

    /**
     * 批量sku信息查询
     *
     * @param skuIdList
     */
    List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIdList);
}