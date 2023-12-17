package com.lmy.live.user.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.dto.UserLoginDTO;
import com.lmy.live.user.dto.UserPhoneDTO;
import com.lmy.live.user.provider.dao.mapper.IUserPhoneMapper;
import com.lmy.live.user.provider.dao.po.UserPhonePO;
import com.lmy.live.user.provider.service.IUserPhoneService;
import com.lmy.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.enums.CommonStatusEum;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.lmy.live.common.interfaces.utils.DESUtils;
import org.lmy.live.id.generate.enums.IdTypeEnum;
import org.lmy.live.id.generate.interfaces.IdGenerateRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserPhoneServiceImpl implements IUserPhoneService {
    private static final Logger logger=LoggerFactory.getLogger(UserPhoneServiceImpl.class);
    @Resource
    private IUserPhoneMapper iUserPhoneMapper;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Resource
    private RedisTemplate redisTemplate;
    @DubboReference
    private IdGenerateRpc idGenerateRpc;
    @Resource
    private IUserService userService;

    @Override
    public UserLoginDTO login(String phone) {
        if(StringUtils.isEmpty(phone)){
            logger.error("[login] phone is null");
            return UserLoginDTO.loginFailed("phone is null");
        }
        //是否注册过
        UserPhoneDTO userPhoneDTO = this.queryByPhone(phone);
        if(userPhoneDTO!=null){
            //是，  创建token，返回userId
            return UserLoginDTO.loginSuccess(userPhoneDTO.getUserId(),createAndSaveLoginToken(userPhoneDTO.getUserId()));
        }
        //如果没注册过，生成user信息，插入手机记录，绑定userId
        return registerAndLogin(phone);
    }

    private UserLoginDTO registerAndLogin(String phone){
        logger.info("[registerAndLogin] start");
        Long userId = idGenerateRpc.getUnSeqId(IdTypeEnum.USER_ID.getCode());
        UserDTO userDTO=new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("lmy用户:"+userId);
        boolean insertUserInfo = userService.insertUserInfo(userDTO);
        logger.info("[registerAndLogin] insertUserInfo:{}",insertUserInfo);
        UserPhonePO userPhonePO=new UserPhonePO();
        userPhonePO.setUserId(userId);
        userPhonePO.setPhone(DESUtils.encrypt(phone));
        userPhonePO.setStatus(CommonStatusEum.VALID_STATUS.getCode());
        iUserPhoneMapper.insert(userPhonePO);
        redisTemplate.delete(userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone));
        return UserLoginDTO.loginSuccess(userId,createAndSaveLoginToken(userId));
    }

    public String createAndSaveLoginToken(Long userId){
        logger.info("[createAndSaveLoginToken] start userId is {}",userId);
        String token  = UUID.randomUUID().toString();
        //token -> xxx:xxx:token redis get userId
        redisTemplate.opsForValue().set(userProviderCacheKeyBuilder.buildUserLoginTokenKey(token),userId,30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        if(userId==null||userId<10000){
            return Collections.emptyList();
        }
        String redisKey = userProviderCacheKeyBuilder.buildUserPhoneListKey(userId);
        List<Object> userPhoneList = redisTemplate.opsForList().range(redisKey, 0, -1);
        if(!CollectionUtils.isEmpty(userPhoneList)){
            if(((UserPhoneDTO)userPhoneList.get(0)).getUserId()==null){
                return Collections.emptyList();
            }
            return userPhoneList.stream().map(x->(UserPhoneDTO)x).collect(Collectors.toList());
        }
        List<UserPhoneDTO> userPhoneDTOS = this.queryUserPhoneByUserIdFromDB(userId);
        //不为空
        if (!CollectionUtils.isEmpty(userPhoneDTOS)){
            userPhoneDTOS.stream().forEach(x->x.setPhone(DESUtils.decrypt(x.getPhone())));
            redisTemplate.opsForList().leftPushAll(redisKey,userPhoneDTOS.toArray());
            redisTemplate.expire(redisKey,30,TimeUnit.MINUTES);
            return userPhoneDTOS;
        }
        //缓存击穿，空值缓存
        redisTemplate.opsForList().leftPush(redisKey,new UserPhoneDTO());
        redisTemplate.expire(redisKey,5,TimeUnit.MINUTES);
        return Collections.emptyList();
    }

    public List<UserPhoneDTO> queryUserPhoneByUserIdFromDB(Long userId){
        logger.info("[queryUserPhoneByUserIdFromDB] start");
        LambdaQueryWrapper<UserPhonePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserPhonePO::getUserId,userId);
        lambdaQueryWrapper.eq(UserPhonePO::getStatus,CommonStatusEum.VALID_STATUS.getCode());
        lambdaQueryWrapper.last("limit 1");
        return ConvertBeanUtils.convertList(iUserPhoneMapper.selectList(lambdaQueryWrapper),UserPhoneDTO.class);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        logger.info("[queryByPhone] start");
        if(StringUtils.isEmpty(phone)){
            return null;
        }
        String redisKey = userProviderCacheKeyBuilder.buildUserPhoneObjKey(phone);
        UserPhoneDTO userPhoneDTO = (UserPhoneDTO) redisTemplate.opsForValue().get(redisKey);
        if(userPhoneDTO!=null){
            if(userPhoneDTO.getUserId()==null){
                return null;
            }
            return userPhoneDTO;
        }
        userPhoneDTO=this.queryByPhoneFromDB(phone);
        if(userPhoneDTO!=null){
            userPhoneDTO.setPhone(DESUtils.decrypt(userPhoneDTO.getPhone()));
            redisTemplate.opsForValue().set(redisKey,userPhoneDTO,30,TimeUnit.MINUTES);
            return userPhoneDTO;
        }
        //缓存击穿,空值缓存
        userPhoneDTO=new UserPhoneDTO();
        redisTemplate.opsForValue().set(redisKey,userPhoneDTO,5,TimeUnit.MINUTES);
        return null;
    }

    public UserPhoneDTO queryByPhoneFromDB(String phone) {
        logger.info("[queryByPhoneFromDB] start");
        LambdaQueryWrapper<UserPhonePO> lambdaQueryWrapper=new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(UserPhonePO::getPhone, DESUtils.encrypt(phone));
        lambdaQueryWrapper.eq(UserPhonePO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        lambdaQueryWrapper.last("limit 1");
        UserPhonePO userPhonePO = iUserPhoneMapper.selectOne(lambdaQueryWrapper);
        UserPhoneDTO convert = ConvertBeanUtils.convert(userPhonePO, UserPhoneDTO.class);
        return convert;
    }
}
