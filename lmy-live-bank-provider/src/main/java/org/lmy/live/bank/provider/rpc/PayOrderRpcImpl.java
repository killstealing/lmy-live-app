package org.lmy.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.interfaces.rpc.IPayOrderRpc;
import org.lmy.live.bank.provider.service.IPayOrderService;

@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {

    @Resource
    private IPayOrderService payOrderService;

    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(payOrderDTO);
    }

    @Override
    public boolean updateOrder(PayOrderDTO payOrderDTO) {
        return payOrderService.updateOrder(payOrderDTO);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {



        return false;
    }
}
