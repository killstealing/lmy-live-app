package org.lmy.live.account.interfaces;

public interface IAccountTokenRPC {
    /**
     * 创建一个登录 token
     *
     * @param userId
     * @return
     */
    String createAndSaveLoginToken(Long userId);

    /**
     * 校验用户 token
     *
     * @param tokenKey
     * @return
     */
    Long getUserIdByToken(String tokenKey);
}
