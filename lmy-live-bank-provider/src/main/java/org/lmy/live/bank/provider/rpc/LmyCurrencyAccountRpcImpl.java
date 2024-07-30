package org.lmy.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;

@DubboService
public class LmyCurrencyAccountRpcImpl implements ILmyCurrencyAccountRpc {

    @Resource
    private ILmyCurrencyAccountService lmyCurrencyAccountService;

    @Override
    public void incr(long userId, int num) {
        lmyCurrencyAccountService.incr(userId,num);
    }

    @Override
    public void decr(long userId, int num) {
        lmyCurrencyAccountService.decr(userId,num);
    }

    @Override
    public LmyCurrencyAccountDTO getByUserId(long userId) {
        return lmyCurrencyAccountService.getByUserId(userId);
    }
}
