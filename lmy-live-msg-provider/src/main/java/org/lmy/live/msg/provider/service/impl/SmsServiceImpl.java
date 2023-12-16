package org.lmy.live.msg.provider.service.impl;

import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.idea.lmy.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.lmy.live.msg.dto.MsgCheckDTO;
import org.lmy.live.msg.enums.MsgSendResultEnum;
import org.lmy.live.msg.provider.config.ThreadPoolManager;
import org.lmy.live.msg.provider.dao.mapper.SmsMapper;
import org.lmy.live.msg.provider.dao.po.SmsPO;
import org.lmy.live.msg.provider.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements ISmsService {

    @Resource
    private SmsMapper smsMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;

    private static final Logger logger= LoggerFactory.getLogger(SmsServiceImpl.class);
    @Override
    public MsgSendResultEnum sendMessage(String phone) {
        //先判断是不是为null
        if(StringUtils.isEmpty(phone)){
            return MsgSendResultEnum.MSG_PARAM_ERROR;
        }
        //生成验证码，4位，6位（取它），有效期（30s，60s），同一个手机号不能重发，redis去存储验证码
        String codeCacheKey=msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        if(redisTemplate.hasKey(codeCacheKey)){
            logger.warn("该手机号短信发送过于频繁，phone is {}", phone);
            return MsgSendResultEnum.SEND_FAIL;
        }
        int code= RandomUtils.nextInt(100000,999999);
        redisTemplate.opsForValue().set(codeCacheKey,code,60, TimeUnit.SECONDS);
        ThreadPoolManager.commonAsyncPool.execute(()->{
            boolean sendStatus = this.mockSendMsg(phone, code);
            if (sendStatus) {
                insertOne(phone,code);
            }
        });
        return MsgSendResultEnum.SEND_SUCCESS;
    }

    @Override
    public MsgCheckDTO checkLoginCode(String phone, Integer code) {
        if(StringUtils.isEmpty(phone)||code==null||code <100000){
            return new MsgCheckDTO(false, "参数异常");
        }
        String codeCacheKey=msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheCode= (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if(cacheCode==null||cacheCode<100000){
            return new MsgCheckDTO(false, "验证码已过期");
        }
        if(cacheCode.equals(code)){
            redisTemplate.delete(codeCacheKey);
            return new MsgCheckDTO(true, "验证码校验成功");
        }
        return new MsgCheckDTO(false,"验证码校验失败");
    }

    @Override
    public void insertOne(String phone, Integer code) {
        SmsPO smsPO=new SmsPO();
        smsPO.setPhone(phone);
        smsPO.setCode(code);
        smsMapper.insert(smsPO);
    }

    private boolean mockSendMsg(String phone, Integer code) {
        boolean result=false;
        try {
            logger.info(" ============= 创建短信发送通道中 ============= ,phone is {},code is {}", phone, code);
            Thread.sleep(1000);
            logger.info(" ============= 短信已经发送成功 ============= ");
            result=true;
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }
}
