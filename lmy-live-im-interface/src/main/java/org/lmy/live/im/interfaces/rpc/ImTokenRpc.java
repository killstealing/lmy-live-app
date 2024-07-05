package org.lmy.live.im.interfaces.rpc;

public interface ImTokenRpc {

    //创建用户登录IM服务的token
    String createImLoginToken(Long userId, int appId);

    //根据token获取userId
    Long getUserIdByToken(String token);
}
