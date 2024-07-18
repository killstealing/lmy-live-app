package org.lmy.live.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.im.interfaces.rpc.ImOnlineRpc;
import org.lmy.live.im.provider.service.ImOnlineService;

@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {

    @Resource
    private ImOnlineService imOnlineService;

    @Override
    public boolean isOnline(Long userId, Integer appId) {
        return imOnlineService.isOnline(userId,appId);
    }

}
