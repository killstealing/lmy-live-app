package org.lmy.live.msg.provider.service.impl;

import com.cloopen.rest.sdk.BodyType;
import com.cloopen.rest.sdk.CCPRestSmsSDK;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.idea.lmy.live.framework.redis.starter.key.MsgProviderCacheKeyBuilder;
import org.lmy.live.msg.dto.MsgCheckDTO;
import org.lmy.live.msg.enums.MsgSendResultEnum;
import org.lmy.live.msg.provider.config.ApplicationProperties;
import org.lmy.live.msg.provider.config.SmsTemplateIdEnum;
import org.lmy.live.msg.provider.config.ThreadPoolManager;
import org.lmy.live.msg.provider.dao.mapper.SmsMapper;
import org.lmy.live.msg.provider.dao.po.SmsPO;
import org.lmy.live.msg.provider.service.ISmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements ISmsService {

    @Resource
    private SmsMapper smsMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Resource
    private MsgProviderCacheKeyBuilder msgProviderCacheKeyBuilder;
    @Resource
    private ApplicationProperties applicationProperties;

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
        int code= RandomUtils.nextInt(1000,9999);
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
        if(StringUtils.isEmpty(phone)||code==null||code <1000){
            return new MsgCheckDTO(false, "参数异常");
        }
        String codeCacheKey=msgProviderCacheKeyBuilder.buildSmsLoginCodeKey(phone);
        Integer cacheCode= (Integer) redisTemplate.opsForValue().get(codeCacheKey);
        if(cacheCode==null||cacheCode<1000){
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
            String serverIp = applicationProperties.getServerIp();
            String serverPort = applicationProperties.getServerPort();
            String accountSId = applicationProperties.getAccountSId();
            String accountToken = applicationProperties.getAccountToken();
            String appId = applicationProperties.getAppId();
            CCPRestSmsSDK sdk = new CCPRestSmsSDK();
            sdk.init(serverIp, serverPort);
            sdk.setAccount(accountSId, accountToken);
            sdk.setAppId(appId);
            sdk.setBodyType(BodyType.Type_JSON);
            String to = applicationProperties.getPhone();
            String templateId= SmsTemplateIdEnum.SMS_LOGIN_CODE_TEMPLATE.getTemplateId();
            String[] datas = {String.valueOf(code),"1"};
//            String subAppend="1234";  //可选 扩展码，四位数字 0~9999
//            String reqId="fadfafas";  //可选 第三方自定义消息id，最大支持32位英文数字，同账号下同一自然天内不允许重复
            HashMap<String, Object> result1 = sdk.sendTemplateSMS(to,templateId,datas);
//            HashMap<String, Object> result1 = sdk.sendTemplateSMS(to,templateId,datas,subAppend,reqId);
            if("000000".equals(result1.get("statusCode"))){
                //正常返回输出data包体信息（map）
                HashMap<String,Object> data = (HashMap<String, Object>) result1.get("data");
                Set<String> keySet = data.keySet();
                for(String key:keySet){
                    Object object = data.get(key);
                    logger.info("phone is {} , key is {},data is {}",to,key,object);
                }
            }else{
                //异常返回输出错误码和错误信息
                logger.error("错误码=" + result1.get("statusCode") +" 错误信息= "+result1.get("statusMsg"));
            }
            result=true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            return result;
        }
    }

    public static void main(String[] args) {

    }
}
