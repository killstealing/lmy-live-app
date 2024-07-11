package org.lmy.live.im.core.server.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {
    private static final Logger logger= LoggerFactory.getLogger(RouterHandlerRpcImpl.class);
    @Override
    public boolean sendMsg(Long userId, String msgBody) {
        logger.info("this is im-core-server");
        return false;
    }
}
