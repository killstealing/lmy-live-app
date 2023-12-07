package com.lmy.live.user.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import com.lmy.live.user.dto.UserDTO;
import com.lmy.live.user.provider.dao.mapper.IUserMapper;
import com.lmy.live.user.provider.dao.po.UserPO;
import com.lmy.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.idea.lmy.live.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.lmy.live.common.interfaces.ConvertBeanUtils;
import org.lmy.live.common.interfaces.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.alibaba.com.caucho.hessian.io.HessianInputFactory.log;

@Service
public class UserServiceImpl implements IUserService {

    private final static Logger logger= LoggerFactory.getLogger(UserServiceImpl.class);

    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String,UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;
    @Resource
    private Producer producer;
    @Value("${lmy.rmq.topic}")
    private String topic;

    @Override
    public UserDTO getByUserId(Long userId) {
        if(userId==null){
            return null;
        }
        String key= userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if(userDTO!=null){
            return userDTO;
        }
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if(userDTO!=null){
            redisTemplate.opsForValue().set(key,userDTO,30, TimeUnit.MINUTES);
        }
        return userDTO;
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        int i = userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
        //立即删除缓存
        redisTemplate.delete(key);
        String tag = "yourMessageTagA";
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        // 普通消息发送。
        Duration messageDelayTime = Duration.ofSeconds(1);
        Message message = provider.newMessageBuilder()
                .setTopic(topic)
                // 设置消息索引键，可根据关键字精确查找某条消息。
//                .setKeys("messageKey")
                // 设置消息Tag，用于消费端根据指定Tag过滤消息。
                .setTag(tag)
                // Set expected delivery timestamp of message.
                .setDeliveryTimestamp(System.currentTimeMillis() + messageDelayTime.toMillis())
                // 消息体。
                .setBody(JSON.toJSONBytes(userDTO))
                .build();
        SendReceipt sendReceipt = null;
        try {
            sendReceipt = producer.send(message);
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
        logger.info("Send message successfully, messageId={}", sendReceipt.getMessageId());
        return i>=0;
    }

    private void sendMessage(){

    }

    @Override
    public boolean insertUserInfo(UserDTO userDTO) {
        int insert = userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return insert>=0;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)){
            return Maps.newHashMap();
        }
        final List<String> keyList=new ArrayList<>();
        userIdList.forEach(userId->{
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });
        List<UserDTO> userDTOS = redisTemplate.opsForValue().multiGet(keyList).stream().filter(x->x!=null).collect(Collectors.toList());
        if(userDTOS!=null&&userDTOS.size()==userIdList.size()){
            return userDTOS.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
        }

        List<Long> userIdInCacheList=userDTOS.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        List<Long> userIdNotInCacheList=userIdList.stream().filter(userId->!userIdInCacheList.contains(userId)).collect(Collectors.toList());

        //多綫程查詢 替換union all
        Map<Long, List<Long>> userIdMap=userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId->userId%100));
        List<UserDTO> dbQueryResult=new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList->{
            dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserIdList),UserDTO.class));
        });
        if(!CollectionUtils.isEmpty(dbQueryResult)) {
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream().collect(Collectors.toMap(userDto -> userProviderCacheKeyBuilder.buildUserInfoKey(userDto.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey: saveCacheMap.keySet()) {
                        operations.expire((K) redisKey, StringUtils.createRandomTime(),TimeUnit.MINUTES);
                    }
                    return null;
                }
            });
            userDTOS.addAll(dbQueryResult);
        }
        return userDTOS.stream().collect(Collectors.toMap(UserDTO::getUserId,x->x));
    }
}
