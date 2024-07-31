package org.lmy.live.bank.provider.service;

import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;

public interface ILmyCurrencyAccountService {
    boolean insertOne(Long userId);
    void incr(Long userId, int num);
    void decr(Long userId,int num);
    LmyCurrencyAccountDTO getByUserId(Long userId);

    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);
}
