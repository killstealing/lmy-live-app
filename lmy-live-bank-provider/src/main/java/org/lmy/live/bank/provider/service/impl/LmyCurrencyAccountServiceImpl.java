package org.lmy.live.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.idea.lmy.live.framework.redis.starter.key.BankProviderCacheKeyBuilder;
import org.lmy.live.bank.interfaces.constants.TradeTypeEnum;
import org.lmy.live.bank.interfaces.dto.AccountTradeReqDTO;
import org.lmy.live.bank.interfaces.dto.AccountTradeRespDTO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.provider.dao.mapper.LmyCurrencyAccountMapper;
import org.lmy.live.bank.provider.dao.po.LmyCurrencyAccountPO;
import org.lmy.live.bank.provider.service.ILmyCurrencyAccountService;
import org.lmy.live.bank.provider.service.ILmyCurrencyTradeService;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
public class LmyCurrencyAccountServiceImpl implements ILmyCurrencyAccountService {
    private static final Logger logger= LoggerFactory.getLogger(LmyCurrencyAccountServiceImpl.class);
    private static ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS, new ArrayBlockingQueue<>(1000));

    @Resource
    private LmyCurrencyAccountMapper currencyAccountMapper;
    @Resource
    private ILmyCurrencyTradeService iLmyCurrencyTradeService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private BankProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public boolean insertOne(Long userId) {
        try {
            LmyCurrencyAccountPO accountPO = new LmyCurrencyAccountPO();
            accountPO.setUserId(userId);
            currencyAccountMapper.insert(accountPO);
            return true;
        } catch (Exception e) {
            logger.error("[LmyCurrencyAccountServiceImpl] insertOne has exception:",e);
        }
        return false;
    }

    @Override
    public void incr(Long userId, int num) {
        currencyAccountMapper.incr(userId,num);
    }

    @Override
    public void decr(Long userId, int num) {
        String cacheKey = cacheKeyBuilder.buildGiftListCacheKey(userId);
        redisTemplate.opsForValue().decrement(cacheKey,num);
        threadPoolExecutor.execute(new Runnable() {
            @Override
            public void run() {
                //分布式架构下，cap理论，可用性和性能，强一致性，柔弱的一致性处理
                //在异步线程池中完成数据库层的扣减和流水记录插入操作，带有事务
                consumeDBHandler(userId,num);
            }
        });
    }

    @Transactional(rollbackFor = Exception.class)
    public void consumeDBHandler(long userId,int num){
        currencyAccountMapper.decr(userId,num);
        iLmyCurrencyTradeService.insertOne(userId,num*-1, TradeTypeEnum.SEND_GIFT_TRADE.getCode());
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

    @Override
    public AccountTradeRespDTO consumerForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        long userId = accountTradeReqDTO.getUserId();
        int num = accountTradeReqDTO.getNum();
        Integer balance = this.getBalance(userId);
        if(balance==null||balance<num){
            return AccountTradeRespDTO.buildFail(userId,"账户余额不足",1);
        }
        this.decr(userId,num);
        return AccountTradeRespDTO.buildSuccess(userId,"扣费成功");
    }

    @Override
    public Integer getBalance(Long userId) {
        return currencyAccountMapper.getBalance(userId);
    }
}
