package org.lmy.live.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import jakarta.annotation.Resource;
import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.provider.dao.mapper.PayOrderMapper;
import org.lmy.live.bank.provider.dao.po.PayOrderPO;
import org.lmy.live.bank.provider.service.IPayOrderService;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PayOrderServiceImpl implements IPayOrderService {

    @Resource
    private PayOrderMapper orderMapper;

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
        orderMapper.update(payOrderPO,updateWrapper);
        return true;
    }
}
