package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.gift.interfaces.rpc.ISkuStockInfoRPC;
import org.lmy.live.gift.provider.service.ISkuStockInfoService;
import org.lmy.live.gift.provider.service.bo.DcrStockNumBO;

/**
 * @Author idea
 * @Date: Created in 09:11 2023/10/5
 * @Description
 */
@DubboService
public class SkuStockInfoRPCImpl implements ISkuStockInfoRPC {

    @Resource
    private ISkuStockInfoService stockInfoService;

    private final int MAX_TRY_TIMES = 5;

    @Override
    public boolean dcrStockNumBySkuId(Long skuId, Integer num) {
        for (int i = 0; i < MAX_TRY_TIMES; i++) {
            DcrStockNumBO dcrStockNumBO = stockInfoService.dcrStockNumBySkuId(skuId, num);
            if (dcrStockNumBO.isNoStock()) {
                return false;
            } else if (dcrStockNumBO.isSuccess()) {
                return true;
            }
        }
        return false;
    }
}
