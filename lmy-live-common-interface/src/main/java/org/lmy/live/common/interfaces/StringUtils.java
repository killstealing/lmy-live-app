package org.lmy.live.common.interfaces;

import java.util.concurrent.ThreadLocalRandom;

public class StringUtils {
    /**
     * 创建随机的过期时间 用于 redis 设置 key 过期
     *
     * @return
     */
    public static int createRandomTime() {
        int randomNumSecond =
                ThreadLocalRandom.current().nextInt(100);
        return randomNumSecond + 30 * 60;
    }
}
