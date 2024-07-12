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
        String objectImServerIp="192.168.6.1:9096";
        String ipAddress = stringRedisTemplate.opsForValue().get(ImCoreServerConstants.IM_BIND_IP_KEY + imMsgBodyDTO.getAppId() + ":" + imMsgBodyDTO.getUserId());
        if(StringUtils.isEmpty(ipAddress)){
            return false;
        }
        RpcContext.getContext().set("ip",ipAddress);
        routerHandlerRpc.sendMsg(imMsgBodyDTO);
        return true;
    }
}
