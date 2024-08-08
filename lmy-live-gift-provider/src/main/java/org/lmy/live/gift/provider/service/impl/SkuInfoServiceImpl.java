package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.gift.provider.dao.mapper.SkuInfoMapper;
import org.lmy.live.gift.provider.dao.po.SkuInfoPO;
import org.lmy.live.gift.provider.service.ISkuInfoService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author idea
 * @Date: Created in 20:08 2023/10/3
 * @Description
 */
@Service
public class SkuInfoServiceImpl implements ISkuInfoService {

    @Resource
    private SkuInfoMapper skuInfoMapper;

    @Override
    public List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuInfoPO> qw = new LambdaQueryWrapper<>();
        qw.in(SkuInfoPO::getSkuId,skuIdList);
        qw.eq(SkuInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return skuInfoMapper.selectList(qw);
    }
}
