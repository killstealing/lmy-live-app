package org.lmy.live.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
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
    public boolean decrV2(long userId, int num) {
        Integer balance = this.lmyCurrencyAccountService.getBalance(userId);
        if(balance>=num){
            return lmyCurrencyAccountService.decr(userId, num);
        }
        return false;
    }

    @Override
    public LmyCurrencyAccountDTO getByUserId(long userId) {
        return lmyCurrencyAccountService.getByUserId(userId);
    }

    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        return lmyCurrencyAccountService.consume(accountTradeReqDTO);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return lmyCurrencyAccountService.consumerForSendGift(accountTradeReqDTO);
    }

    @Override
    public Integer getBalance(long userId) {
        return lmyCurrencyAccountService.getBalance(userId);
    }
}
