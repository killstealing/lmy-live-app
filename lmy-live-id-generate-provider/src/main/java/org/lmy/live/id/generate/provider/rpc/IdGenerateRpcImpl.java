package org.lmy.live.id.generate.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.id.generate.interfaces.IdGenerateRpc;
import org.lmy.live.id.generate.provider.service.IdGenerateService;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {
    @Resource
    private IdGenerateService idGenerateService;

    @Override
    public Long getSeqId(Integer code) {
        return idGenerateService.getSeqId(code);
    }

    @Override
    public Long getUnSeqId(Integer code) {
        return idGenerateService.getUnSeqId(code);
    }
}
