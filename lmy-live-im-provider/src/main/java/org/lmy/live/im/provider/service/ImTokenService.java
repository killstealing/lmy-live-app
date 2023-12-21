package org.lmy.live.im.provider.service;

public interface ImTokenService {

    String createImLoginToken(Long userId, int appId);

    Long getUserIdByToken(String token);
}
