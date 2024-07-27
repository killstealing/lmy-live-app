package org.lmy.live.im.router.provider.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.lmy.live.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.lmy.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.provider.service.ImRouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static final Logger logger= LoggerFactory.getLogger(ImRouterServiceImpl.class);
    @Override
    public boolean sendMsg(ImMsgBodyDTO imMsgBodyDTO) {
        logger.info("[ImRouterServiceImpl] send Msg, userid is {},msgBody is {}",imMsgBodyDTO.getUserId(),imMsgBodyDTO);
        String ipAddress = stringRedisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyDTO.getAppId() + ":" + imMsgBodyDTO.getUserId());
        if(StringUtils.isEmpty(ipAddress)){
            return false;
        }
        ipAddress=ipAddress.substring(0,ipAddress.indexOf("%"));
        logger.info("[ImRouterServiceImpl] sendMsg ipAddress is {}",ipAddress);
        RpcContext.getContext().set("ip",ipAddress);
        routerHandlerRpc.sendMsg(imMsgBodyDTO);
        return true;
    }

    @Override
    public void batchSendMsg(List<ImMsgBodyDTO> msgBodyDTOS) {
        List<Long> userIdList = msgBodyDTOS.stream().map(ImMsgBodyDTO::getUserId).collect(Collectors.toList());
        //根据userId将不同的userId的ImMsgBody分类存入map
        Map<Long, ImMsgBodyDTO> userIdMap = msgBodyDTOS.stream().collect(Collectors.toMap(ImMsgBodyDTO::getUserId, x -> x));
        //保证整个list集合的appId得是同一个
        Integer appId = msgBodyDTOS.get(0).getAppId();
        List<String> cacheKeyList=new ArrayList<>();
        userIdList.forEach(userId->{
            String cacheKey=ImCoreServerConstants.IM_BIND_IP_KEY+appId+":"+userId;
            cacheKeyList.add(cacheKey);
        });
        //批量取出每个用户绑定的IP地址
        List<String> ipList = stringRedisTemplate.opsForValue().multiGet(cacheKeyList).stream().filter(ip->ip!=null).collect(Collectors.toList());
        Map<String,List<Long>> userIdIPListMap=new HashMap<>();
        ipList.forEach(ip->{
            String currentIp=ip.substring(0,ip.indexOf("%"));
            Long userId= Long.valueOf(ip.substring(ip.indexOf("%")+1));
            List<Long> userIds = userIdIPListMap.get(currentIp);
            if(userIds==null){
                userIds=new ArrayList<>();
            }
            userIds.add(userId);
            userIdIPListMap.put(currentIp,userIds);
        });

        //将连接同一个ip地址的ImMsgBody组装到同一个list集合中，然后进行统一的发送
        for (String ip : userIdIPListMap.keySet()) {
            RpcContext.getContext().set("ip",ip);
            List<Long> userIdSendList = userIdIPListMap.get(ip);
            List<ImMsgBodyDTO> imMsgBodyDTOList=new ArrayList<>();
            userIdSendList.forEach(userId->{
                ImMsgBodyDTO imMsgBodyDTO = userIdMap.get(userId);
                imMsgBodyDTOList.add(imMsgBodyDTO);
            });
            routerHandlerRpc.batchSendMsg(imMsgBodyDTOList);
        }
    }
}
