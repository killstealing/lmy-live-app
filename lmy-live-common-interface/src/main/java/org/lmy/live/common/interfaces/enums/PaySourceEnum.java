package org.lmy.live.common.interfaces.enums;

public enum PaySourceEnum {

    LMY_LIVING_ROOM(1, "旗鱼直播间内支付"),
    LMY_USER_CENTER(2, "用户中心支付");

    PaySourceEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static PaySourceEnum find(int code) {
        for (PaySourceEnum sourceEnum : PaySourceEnum.values()) {
            if (sourceEnum.code == code) {
                return sourceEnum;
            }
        }
        return null;
    }

    private int code;
    private String desc;

    public int getCode() {
        return code;
    }
}
