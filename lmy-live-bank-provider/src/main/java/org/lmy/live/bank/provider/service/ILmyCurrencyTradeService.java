package org.lmy.live.bank.provider.service;

public interface ILmyCurrencyTradeService {
    /**
     * 插入一条流水记录
     *
     * @param userId
     * @param num
     * @param type
     * @return
     */
    boolean insertOne(long userId,int num,int type);
}
