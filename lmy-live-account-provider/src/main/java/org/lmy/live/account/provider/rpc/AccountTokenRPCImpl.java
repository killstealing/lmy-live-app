package org.lmy.live.account.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.lmy.live.account.interfaces.IAccountTokenRPC;
import org.lmy.live.account.provider.service.IAccountTokenService;

@DubboService
public class AccountTokenRPCImpl implements IAccountTokenRPC {

    @Resource
    private IAccountTokenService accountTokenService;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        return accountTokenService.createAndSaveLoginToken(userId);
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        return accountTokenService.getUserIdByToken(tokenKey);
    }
}
