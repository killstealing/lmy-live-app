package org.lmy.live.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.im.interfaces.dto.ImMsgBodyDTO;
import org.lmy.live.im.router.interfaces.rpc.ImRouterRpc;
import org.lmy.live.im.router.provider.service.ImRouterService;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;

    @Override
    public boolean sendMsg(ImMsgBodyDTO msgBody) {
        return imRouterService.sendMsg(msgBody);
    }
}
