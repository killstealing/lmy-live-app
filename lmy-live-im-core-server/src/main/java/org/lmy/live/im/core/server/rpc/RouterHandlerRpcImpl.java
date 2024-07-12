package org.lmy.live.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.lmy.live.im.core.server.service.IRouterHandlerService;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {
    private static final Logger logger= LoggerFactory.getLogger(RouterHandlerRpcImpl.class);

    @Resource
    private IRouterHandlerService routerHandlerService;
    @Override
    public boolean sendMsg(ImMsgBodyDTO msgBody) {
        logger.info("this is im-core-server");
        routerHandlerService.onReceive(msgBody);
        return false;
    }
}
