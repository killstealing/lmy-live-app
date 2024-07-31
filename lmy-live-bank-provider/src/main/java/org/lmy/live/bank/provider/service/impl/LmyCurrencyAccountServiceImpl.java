package org.lmy.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.provider.dao.mapper.LmyCurrencyAccountMapper;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyAccountPO;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
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

    @Override
    public AccountTradeRespDTO consume(AccountTradeReqDTO accountTradeReqDTO) {
        long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        //首先判断账户余额是否充足，考虑无记录的情况
        LmyCurrencyAccountDTO accountDTO=this.getByUserId(userId);
        if(accountDTO==null){
            return AccountTradeRespDTO.buildFail(userId,"账号未初始化",1);
        }
        if(!accountDTO.getStatus().equals(CommonStatusEum.VALID_STATUS.getCode())){
            return AccountTradeRespDTO.buildFail(userId,"账号异常",2);
        }
        if(accountDTO.getCurrentBalance()-accountTradeReqDTO.getNum()<0){
            return AccountTradeRespDTO.buildFail(userId,"余额不足",3);
        }
        //todo 流水记录？
        //大并发请求场景，1000个直播间，500人，50w人在线，20%的人送礼，10w人在线触发送礼行为，
        //DB扛不住
        //1.MySQL换成写入性能相对较高的数据库
        //2.我们能不能从业务上去进行优化，用户送礼都在直播间，大家都连接上了im服务器，router层扩容（50台），im-core-server层（100台），RocketMQ削峰，
        // 消费端也可以水平扩容
        //3.我们客户端发起送礼行为的时候，同步校验（校验账户余额是否足够，余额放入到redis中），
        //4.拦截下大部分的请求，如果余额不足，（接口还得做防止重复点击，客户端也要防重复）
        //5.同步送礼接口，只完成简单的余额校验，发送mq，在mq的异步操作里面，完成二次余额校验，余额扣减，礼物发送
        //6.如果余额不足，是不是可以利用im，反向通知发送方
        // todo 性能问题
        //扣减余额
        this.decr(userId,num);
        return AccountTradeRespDTO.buildSuccess(userId,"扣费成功");
    }
}
