package org.lmy.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.im.router.provider.service.ImRouterService;

public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;

    @Override
    public boolean sendMsg(Long userId, String msgBody) {
        return imRouterService.sendMsg(userId,msgBody);
    }
}
