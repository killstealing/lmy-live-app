package org.lmy.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.bank.provider.dao.mapper.LmyCurrencyTradeMapper;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyTradePO;
import org.lmy.live.bank.provider.service.ILmyCurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LmyCurrencyTradeServiceImpl implements ILmyCurrencyTradeService {
    private static final Logger logger= LoggerFactory.getLogger(LmyCurrencyTradeServiceImpl.class);
    @Resource
    private LmyCurrencyTradeMapper lmyCurrencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try {
            LmyCurrencyTradePO lmyCurrencyTradePO=new LmyCurrencyTradePO();
            lmyCurrencyTradePO.setUserId(userId);
            lmyCurrencyTradePO.setNum(num);
            lmyCurrencyTradePO.setType(type);
            lmyCurrencyTradeMapper.insert(lmyCurrencyTradePO);
            return true;
        }catch (Exception e){
            logger.error("[LmyCurrencyTradeServiceImpl] insertOne has exception :",e);
        }
        return false;
    }
}
