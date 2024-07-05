package org.lmy.live.im.core.server.common;

import io.netty.util.AttributeKey;

public class ImContextAttr {
    public static AttributeKey<Long> USER_ID=AttributeKey.valueOf("userId");
    public static AttributeKey<Integer> APP_ID=AttributeKey.valueOf("appId");
}
