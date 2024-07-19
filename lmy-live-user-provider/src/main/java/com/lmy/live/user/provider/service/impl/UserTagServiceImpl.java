package com.lmy.live.user.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import com.lmy.live.user.constants.CacheAsyncDeleteCode;
import com.lmy.live.user.constants.UserProviderTopicNames;
import com.lmy.live.user.constants.UserTagFieldNameConstants;
import com.lmy.live.user.constants.UserTagsEnum;
import com.lmy.live.user.dto.UserCacheAsyncDeleteDTO;
import com.lmy.live.user.dto.UserTagDTO;
import com.lmy.live.user.provider.dao.mapper.IUserTagMapper;
import com.lmy.live.user.provider.dao.po.UserTagPO;
import com.lmy.live.user.provider.service.IUserTagService;
import com.lmy.live.user.provider.utils.TagInfoUtils;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.utils.ConvertBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserTagServiceImpl implements IUserTagService {

    private static final Logger logger= LoggerFactory.getLogger(UserTagServiceImpl.class);

    @Resource
    private IUserTagMapper iUserTagMapper;

    @Resource
    private RedisTemplate<String,UserTagDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;

    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        // 尝试update true, return
        //设置了标签， 没有记录（两种失败场景）
        //select is null, insert return , update
        String userTagKey = userProviderCacheKeyBuilder.buildUserTagKey(userId);
        boolean updateResult = iUserTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(updateResult){
            this.deleteUserTagDTOFromRedis(userId);
            return true;
        }
        String setNXResult = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();
                return (String) connection.execute("set", keySerializer.serialize(userTagKey),
                        valueSerializer.serialize("-1"),
                        "NX".getBytes(StandardCharsets.UTF_8),
                        "EX".getBytes(StandardCharsets.UTF_8),
                        "10".getBytes(StandardCharsets.UTF_8));
            }
        });
        if(!"OK".equalsIgnoreCase(setNXResult)){
            return false;
        }
        UserTagPO userTagPO = iUserTagMapper.selectById(userId);
        //这种是有这条记录， 但是已经有这个标签了， 所以失败
        if(userTagPO!=null){
            return false;
        }
        userTagPO=new UserTagPO();
        userTagPO.setUserId(userId);
        iUserTagMapper.insert(userTagPO);
        updateResult = iUserTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag())>0;
        redisTemplate.delete(userTagKey);
        logger.info("update成功");
        return updateResult;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean result = iUserTagMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(result){
            this.deleteUserTagDTOFromRedis(userId);
        }
        return result;
    }

    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        if(userId==null){
            logger.error("[containTag] userId is null");
            return false;
        }
        UserTagDTO userTagDTO = this.queryByUserIdFromRedis(userId);
        if(userTagDTO==null){
            logger.error("[containTag] userTagDTO is null");
            return false;
        }
        if(UserTagFieldNameConstants.TAG_INFO_01.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagDTO.getTagInfo01(),userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_02.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagDTO.getTagInfo02(),userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_03.equalsIgnoreCase(userTagsEnum.getFieldName())){
            return TagInfoUtils.ifContain(userTagDTO.getTagInfo03(),userTagsEnum.getTag());
        }
        return false;
    }


    private UserTagDTO queryByUserIdFromRedis(Long userId){
        String userTagKey = userProviderCacheKeyBuilder.buildUserTagKey(userId);
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(userTagKey);
        if(userTagDTO!=null){
            return userTagDTO;
        }
        UserTagPO userTagPO = iUserTagMapper.selectById(userId);
        if(userTagPO==null){
            return null;
        }
        userTagDTO = ConvertBeanUtils.convert(userTagPO, UserTagDTO.class);
        redisTemplate.opsForValue().set(userTagKey, userTagDTO);
        redisTemplate.expire(userTagKey,30, TimeUnit.MINUTES);
        return userTagDTO;
    }

    /**
     * 从redis中删除用户标签对象
     *
     * @param userId
     */
    private void deleteUserTagDTOFromRedis(Long userId) {
        String redisKey = userProviderCacheKeyBuilder.buildUserTagKey(userId);
        redisTemplate.delete(redisKey);

        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
        userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode());
        Map<String,Object> jsonParam = new HashMap<>();
        jsonParam.put("userId",userId);
        userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));

        Message message = new Message();
        message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);
        message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
        //延迟一秒进行缓存的二次删除
        message.setDelayTimeLevel(1);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
