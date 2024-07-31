package org.lmy.live.bank.interfaces.rpc;

import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;

public interface ILmyCurrencyAccountRpc {
    /**
     * 增加虚拟币
     *
     * @param userId
     * @param num
     */
    void incr(long userId,int num);

    /**
     * 扣减虚拟币
     *
     * @param userId
     * @param num
     */
    void decr(long userId,int num);

    /**
     * 查询账户
     *
     * @param userId
     * @return
     */
    LmyCurrencyAccountDTO getByUserId(long userId);

    /**
     * 底层需要判断用户余额是否充足，充足则扣减，不足则拦截
     *
     * @param accountTradeReqDTO
     */
    AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO);
}
