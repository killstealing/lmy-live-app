package org.idea.lmy.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IGiftService;
import org.idea.lmy.live.api.vo.resp.GiftConfigVO;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.GiftConfigDTO;
import org.lmy.live.gift.interfaces.rpc.IGiftConfigRpc;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftServiceImpl implements IGiftService {

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigRpc.queryGiftList();
        List<GiftConfigVO> giftConfigVOList = ConvertBeanUtils.convertList(giftConfigDTOS, GiftConfigVO.class);
        return giftConfigVOList;
    }
}
