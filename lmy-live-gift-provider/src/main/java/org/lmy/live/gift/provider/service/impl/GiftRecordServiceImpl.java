package org.lmy.live.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.GiftRecordDTO;
import org.lmy.live.gift.provider.dao.mapper.GiftRecordMapper;
import org.lmy.live.gift.provider.dao.po.GiftRecordPO;
import org.lmy.live.gift.provider.service.IGiftRecordService;
import org.springframework.stereotype.Service;

@Service
public class GiftRecordServiceImpl implements IGiftRecordService {

    @Resource
    private GiftRecordMapper giftRecordMapper;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO, GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
