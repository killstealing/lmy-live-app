package org.lmy.live.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.SkuDetailInfoDTO;
import org.lmy.live.gift.interfaces.dto.SkuInfoDTO;
import org.lmy.live.gift.interfaces.rpc.ISkuInfoRPC;
import org.lmy.live.gift.provider.dao.po.SkuInfoPO;
import org.lmy.live.gift.provider.service.IAnchorShopInfoService;
import org.lmy.live.gift.provider.service.ISkuInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:11 2023/10/3
 * @Description
 */
@Service
public class SkuInfoRPCImpl implements ISkuInfoRPC {

    @Resource
    private ISkuInfoService skuInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;

    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        List<Long> skuIdLIst = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdLIst);
        return ConvertBeanUtils.convertList(skuInfoPOS,SkuInfoDTO.class);
    }

    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId) {
        SkuInfoPO skuInfoPO = skuInfoService.queryBySkuIdFromCache(skuId);
        return ConvertBeanUtils.convert(skuInfoPO,SkuDetailInfoDTO.class);
    }
}
