package org.lmy.live.bank.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.lmy.live.bank.interfaces.constants.PayProductTypeEnum;
import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.provider.dao.mapper.PayOrderMapper;
import org.lmy.live.bank.provider.dao.po.PayOrderPO;
import org.lmy.live.bank.provider.dao.po.PayTopicPO;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;
import org.lmy.live.bank.provider.service.IPayOrderService;
import org.lmy.live.bank.provider.service.IPayProductService;
import org.lmy.live.bank.provider.service.IPayTopicService;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PayOrderServiceImpl implements IPayOrderService {
    private static final Logger logger= LoggerFactory.getLogger(PayOrderServiceImpl.class);
    @Resource
    private PayOrderMapper orderMapper;

    @Resource
    private IPayTopicService payTopicService;
    @Resource
    private MQProducer mqProducer;

    @Resource
    private ILmyCurrencyAccountService accountService;
    @Resource
    private IPayProductService payProductService;

    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        PayOrderPO payOrderPO = ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class);
        String orderId = UUID.randomUUID().toString();
        payOrderPO.setOrderId(orderId);
        orderMapper.insert(payOrderPO);
        return orderId;
    }

    @Override
    public boolean updateOrder(PayOrderDTO payOrderDTO) {
        LambdaUpdateWrapper<PayOrderPO> updateWrapper=new LambdaUpdateWrapper<>();
        updateWrapper.eq(PayOrderPO::getOrderId,payOrderDTO.getOrderId());
        PayOrderPO payOrderPO = ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class);
        int update = orderMapper.update(payOrderPO, updateWrapper);
        return update<0;
    }

    @Override
    public PayOrderPO queryByOrderId(String orderId) {
        LambdaQueryWrapper<PayOrderPO> queryWrapper=new LambdaQueryWrapper<>();
        queryWrapper.eq(PayOrderPO::getOrderId,orderId);
        queryWrapper.last("limit 1");
        return orderMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        //假设 支付成功后，要发送消息通知 -》 msg-provider
        //假设 支付成功后，要修改用户的vip经验值
        //发mq
        //中台服务，支付的对接方 10几种服务，pay-notify-topic
        PayOrderPO payOrderPO = this.queryByOrderId(payOrderDTO.getOrderId());
        if(payOrderPO==null){
            logger.error("[PayOrderServiceImpl] payNotify payOrderPO is null");
            return false;
        }
        PayTopicPO topicPO = payTopicService.getByCode(payOrderDTO.getBizCode());
        if(topicPO==null){
            logger.error("[PayOrderServiceImpl] payNotify topicPO is null");
            return false;
        }
        this.payNotifyHandler(payOrderPO);
        Message message=new Message();
        message.setTopic(topicPO.getTopic());
        message.setBody(JSON.toJSONBytes(payOrderPO));
        try {
            SendResult sendResult = mqProducer.send(message);
            logger.info("[PayOrderServiceImpl] payNotify sendResult:{}",sendResult);
        } catch (Exception e) {
            logger.error("[PayOrderServiceImpl] payNotify mq send has exception:",e);
        }

        return true;
    }

    private void payNotifyHandler(PayOrderPO payOrderPO){
        PayOrderDTO payOrderDTO = ConvertBeanUtils.convert(payOrderPO, PayOrderDTO.class);
        this.updateOrder(payOrderDTO);
        PayProductDTO productDTO = payProductService.getProductById(payOrderPO.getProductId());
        if(productDTO!=null&& PayProductTypeEnum.QIYU_COIN.getCode()==productDTO.getType()){
            Integer coin = JSON.parseObject(productDTO.getExtra()).getInteger("coin");
            accountService.incr(payOrderDTO.getUserId(),coin);
        }
    }
}
