package com.lmy.live.user.provider.utils;

public class TagInfoUtils {
    /**
     *
     * @param tagInfo 用户当前的标签值
     * @param matchTag 被查询是否匹配的标签值
     * @return
     */
    public static boolean ifContain(Long tagInfo,Long matchTag){
        return tagInfo!=null&&matchTag!=null&& (tagInfo &matchTag)==matchTag;
    }
}
