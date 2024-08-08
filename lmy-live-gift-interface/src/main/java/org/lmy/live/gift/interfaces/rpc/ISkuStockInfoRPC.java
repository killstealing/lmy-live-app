package org.lmy.live.gift.interfaces.rpc;

/**
 * @Author idea
 * @Date: Created in 09:11 2023/10/5
 * @Description
 */
public interface ISkuStockInfoRPC {

    /**
     * 根据skuId更新库存值
     * @param skuId
     * @param num
     */
    boolean dcrStockNumBySkuId(Long skuId,Integer num);
}
