package org.lmy.live.id.generate.provider.rpc;

import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.id.generate.interfaces.IdGenerateRpc;

@DubboService
public class IdGenerateRpcImpl implements IdGenerateRpc {



    @Override
    public Long increaseSeqId(int code) {
        return null;
    }

    @Override
    public Long increaseUnSeqId(int code) {
        return null;
    }

    @Override
    public String increaseSeqStrId(int code) {
        return null;
    }
}
