package org.idea.lmy.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.idea.lmy.live.api.error.ApiErrorEnum;
import org.idea.lmy.live.api.service.IGiftService;
import org.idea.lmy.live.api.vo.req.GiftReqVO;
import org.idea.lmy.live.api.vo.resp.GiftConfigVO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.common.interfaces.dto.SendGiftMq;
import org.lmy.live.common.interfaces.topic.GiftProviderTopicNames;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.gift.interfaces.dto.GiftConfigDTO;
import org.lmy.live.gift.interfaces.rpc.IGiftConfigRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GiftServiceImpl implements IGiftService {
    private static final Logger logger= LoggerFactory.getLogger(GiftServiceImpl.class);

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;

    @DubboReference
    private ILmyCurrencyAccountRpc currencyAccountRpc;

    @Resource
    private MQProducer producer;

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
//        AccountTradeReqDTO accountTradeReqDTO=new AccountTradeReqDTO();
//        //TODO
////        accountTradeReqDTO.setUserId(giftReqVO.getSenderUserId());
//        accountTradeReqDTO.setUserId(LmyRequestContext.getUserId());
//        accountTradeReqDTO.setNum(giftConfigDTO.getPrice());
//        AccountTradeRespDTO accountTradeRespDTO = currencyAccountRpc.consumeForSendGift(accountTradeReqDTO);
//        ErrorAssert.isTure(accountTradeRespDTO!=null&&accountTradeRespDTO.isSuccess(),ApiErrorEnum.SEND_GIFT_ERROR);

        SendGiftMq sendGiftMq=new SendGiftMq();
        sendGiftMq.setUserId(LmyRequestContext.getUserId());
        sendGiftMq.setGiftId(giftId);
        sendGiftMq.setRoomId(giftReqVO.getRoomId());
        sendGiftMq.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        sendGiftMq.setType(giftReqVO.getType());
        //避免重复消费
        sendGiftMq.setUuid(UUID.randomUUID().toString());
        Message message=new Message();
        message.setBody(JSON.toJSONBytes(sendGiftMq));
        message.setTopic(GiftProviderTopicNames.SEND_GIFT);
        try {
            SendResult sendResult = producer.send(message);
            logger.info("[GiftServiceImpl] sendGift sendResult is {}",sendResult);
        } catch (Exception e) {
            logger.error("[GiftServiceImpl] sendGift has exception: ",e);
        }
        return true;
    }
}
