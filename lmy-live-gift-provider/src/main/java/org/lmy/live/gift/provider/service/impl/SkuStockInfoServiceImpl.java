package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.gift.provider.dao.mapper.SkuStockInfoMapper;
import org.lmy.live.gift.provider.dao.po.SkuStockInfoPO;
import org.lmy.live.gift.provider.service.ISkuStockInfoService;
import org.lmy.live.gift.provider.service.bo.DcrStockNumBO;
import org.springframework.stereotype.Service;

/**
 * @Author idea
 * @Date: Created in 08:56 2023/10/5
 * @Description
 */
@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {

    @Resource
    private SkuStockInfoMapper skuStockInfoMapper;

    @Override
    public DcrStockNumBO dcrStockNumBySkuId(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = this.queryBySkuId(skuId);
        DcrStockNumBO dcrStockNumBO = new DcrStockNumBO();
        if(skuStockInfoPO==null){
            dcrStockNumBO.setNoStock(true);
            dcrStockNumBO.setSuccess(false);
            return dcrStockNumBO;
        }
        if (skuStockInfoPO.getStockNum() == 0 || skuStockInfoPO.getStockNum() - num < 0) {
            dcrStockNumBO.setNoStock(true);
            dcrStockNumBO.setSuccess(false);
            return dcrStockNumBO;
        }
        dcrStockNumBO.setNoStock(false);
        boolean updateState = skuStockInfoMapper.dcrStockNumBySkuId(skuId, num, skuStockInfoPO.getVersion()) > 0;
        dcrStockNumBO.setSuccess(updateState);
        return dcrStockNumBO;
    }

    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> qw = new LambdaQueryWrapper<>();
        qw.eq(SkuStockInfoPO::getSkuId, skuId);
        qw.eq(SkuStockInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        qw.last("limit 1");
        return skuStockInfoMapper.selectOne(qw);
    }
}
