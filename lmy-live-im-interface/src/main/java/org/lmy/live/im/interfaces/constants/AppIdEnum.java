package org.lmy.live.im.interfaces.constants;

public enum AppIdEnum {
    LMY_LIVE_BIZ(10001,"LMY直播业务");
    int code;
    String desc;

    AppIdEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
