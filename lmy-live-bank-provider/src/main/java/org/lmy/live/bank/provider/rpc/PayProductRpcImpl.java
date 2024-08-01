package org.lmy.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.interfaces.rpc.IPayProductRpc;
import org.lmy.live.bank.provider.service.IPayProductService;

import java.util.List;

@DubboService
public class PayProductRpcImpl implements IPayProductRpc {

    @Resource
    private IPayProductService payProductService;

    @Override
    public List<PayProductDTO> getProductList(Integer type) {
        return payProductService.getProductList(type);
    }
}
