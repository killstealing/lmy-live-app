package org.lmy.live.im.interfaces.rpc;

public interface ImTokenRpc {

    String createImLoginToken(Long userId, int appId);

    Long getUserIdByToken(String token);
}
