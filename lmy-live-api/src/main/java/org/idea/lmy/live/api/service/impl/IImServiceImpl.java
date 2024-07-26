package org.idea.lmy.live.api.service.impl;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IImService;
import org.idea.lmy.live.api.vo.resp.ImConfigVO;
import org.lmy.live.im.interfaces.constants.AppIdEnum;
import org.lmy.live.im.interfaces.rpc.ImTokenRpc;
import org.lmy.live.web.starter.LmyRequestContext;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class IImServiceImpl implements IImService {

    @DubboReference
    private ImTokenRpc imTokenRpc;

    @Resource
    private DiscoveryClient discoveryClient;

    @Override
    public ImConfigVO getImConfig() {
        ImConfigVO imConfigVO=new ImConfigVO();
        imConfigVO.setToken(imTokenRpc.createImLoginToken(LmyRequestContext.getUserId(), AppIdEnum.LMY_LIVE_BIZ.getCode()));
        buildImServerAddress(imConfigVO);
        return imConfigVO;
    }

    private void buildImServerAddress(ImConfigVO imConfigVO){
        List<ServiceInstance> instances = discoveryClient.getInstances("lmy-live-im-core-server");
        Collections.shuffle(instances);
        ServiceInstance serviceInstance = instances.get(0);
        imConfigVO.setWsImServerAddress(serviceInstance.getHost()+":8055");
        imConfigVO.setTcpImServerAddress(serviceInstance.getHost()+":8056");
    }
}
