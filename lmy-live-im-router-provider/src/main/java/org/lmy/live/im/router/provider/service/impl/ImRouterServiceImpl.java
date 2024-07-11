package org.lmy.live.im.router.provider.service.impl;

import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.rpc.RpcContext;
import org.lmy.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.lmy.live.im.router.provider.service.ImRouterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImRouterServiceImpl implements ImRouterService {

    @DubboReference
    private IRouterHandlerRpc routerHandlerRpc;

    private static final Logger logger= LoggerFactory.getLogger(ImRouterServiceImpl.class);
    @Override
    public boolean sendMsg(Long userId, String msgBody) {
        logger.info("[ImRouterServiceImpl] send Msg, userid is {},msgBody is {}",userId,msgBody);
        String objectImServerIp="192.168.6.1:9096";
        RpcContext.getContext().set("ip",objectImServerIp);
        routerHandlerRpc.sendMsg(userId, msgBody);
        return false;
    }
}
