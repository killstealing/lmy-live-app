package org.idea.lmy.live.api.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.error.ApiErrorEnum;
import org.idea.lmy.live.api.service.IGiftService;
import org.idea.lmy.live.api.vo.req.GiftReqVO;
import org.idea.lmy.live.api.vo.resp.GiftConfigVO;
import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.GiftConfigDTO;
import org.lmy.live.gift.interfaces.rpc.IGiftConfigRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiftServiceImpl implements IGiftService {

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;

    @DubboReference
    private ILmyCurrencyAccountRpc currencyAccountRpc;

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigRpc.queryGiftList();
        List<GiftConfigVO> giftConfigVOList = ConvertBeanUtils.convertList(giftConfigDTOS, GiftConfigVO.class);
        return giftConfigVOList;
    }

    @Override
    public boolean sendGift(GiftReqVO giftReqVO) {
        int giftId = giftReqVO.getGiftId();
        GiftConfigDTO giftConfigDTO = giftConfigRpc.getByGiftId(giftId);
        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        AccountTradeReqDTO accountTradeReqDTO=new AccountTradeReqDTO();
        //TODO
//        accountTradeReqDTO.setUserId(giftReqVO.getSenderUserId());
        accountTradeReqDTO.setUserId(LmyRequestContext.getUserId());
        accountTradeReqDTO.setNum(giftConfigDTO.getPrice());
        AccountTradeRespDTO accountTradeRespDTO = currencyAccountRpc.consumeForSendGift(accountTradeReqDTO);
        ErrorAssert.isTure(accountTradeRespDTO!=null&&accountTradeRespDTO.isSuccess(),ApiErrorEnum.SEND_GIFT_ERROR);
        return true;
    }
}
