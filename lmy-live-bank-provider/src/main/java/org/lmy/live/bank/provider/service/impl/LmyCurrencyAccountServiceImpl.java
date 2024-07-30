package org.lmy.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.provider.dao.mapper.LmyCurrencyAccountMapper;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyAccountPO;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.springframework.stereotype.Service;

@Service
public class LmyCurrencyAccountServiceImpl implements ILmyCurrencyAccountService {

    @Resource
    private LmyCurrencyAccountMapper currencyAccountMapper;

    @Override
    public boolean insertOne(Long userId) {
        try {
            LmyCurrencyAccountPO accountPO = new LmyCurrencyAccountPO();
            accountPO.setUserId(userId);
            currencyAccountMapper.insert(accountPO);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void incr(Long userId, int num) {
        currencyAccountMapper.incr(userId,num);
    }

    @Override
    public void decr(Long userId, int num) {
        currencyAccountMapper.decr(userId,num);
    }

    @Override
    public LmyCurrencyAccountDTO getByUserId(Long userId) {
        LmyCurrencyAccountPO lmyCurrencyAccountPO = currencyAccountMapper.selectById(userId);
        LmyCurrencyAccountDTO lmyCurrencyAccountDTO = ConvertBeanUtils.convert(lmyCurrencyAccountPO, LmyCurrencyAccountDTO.class);
        return lmyCurrencyAccountDTO;
    }
}
