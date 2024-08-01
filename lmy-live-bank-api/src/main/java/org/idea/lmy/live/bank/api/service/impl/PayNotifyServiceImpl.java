package org.idea.lmy.live.bank.api.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.bank.api.service.IPayNotifyService;
import org.idea.lmy.live.bank.api.vo.PayNotifyVO;
import org.lmy.live.bank.interfaces.constants.OrderStatusEnum;
import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.interfaces.rpc.IPayOrderRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayNotifyServiceImpl implements IPayNotifyService {
    private static final Logger logger= LoggerFactory.getLogger(PayNotifyServiceImpl.class);

    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public String notifyHandler(String paramJson) {
        PayNotifyVO payNotifyVO = JSON.parseObject(paramJson, PayNotifyVO.class);
        PayOrderDTO payOrderDTO=new PayOrderDTO();
        payOrderDTO.setOrderId(payNotifyVO.getOrderId());
        payOrderDTO.setBizCode(payNotifyVO.getBizCode());
        payOrderDTO.setStatus(OrderStatusEnum.PAID.getCode());
        boolean updateSuccess = payOrderRpc.payNotify(payOrderDTO);
        return updateSuccess?"success":"fail";
    }
}
