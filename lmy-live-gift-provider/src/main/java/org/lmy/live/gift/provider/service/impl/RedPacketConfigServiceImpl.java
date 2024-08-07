package org.lmy.live.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.RedPacketConfigRespDTO;
import org.lmy.live.gift.provider.dao.mapper.RedPacketConfigMapper;
import org.lmy.live.gift.provider.dao.po.RedPacketConfigPO;
import org.lmy.live.gift.provider.service.IRedPacketConfigService;
import org.springframework.stereotype.Service;

@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {

    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(RedPacketConfigPO::getAnchorId,anchorId);
        lambdaQueryWrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        lambdaQueryWrapper.orderByDesc(RedPacketConfigPO::getStartTime);
        lambdaQueryWrapper.last("limit 1");
        RedPacketConfigPO redPacketConfigPO = redPacketConfigMapper.selectOne(lambdaQueryWrapper);
        RedPacketConfigRespDTO redPacketConfigRespDTO = ConvertBeanUtils.convert(redPacketConfigPO, RedPacketConfigRespDTO.class);
        return redPacketConfigRespDTO;
    }
}
