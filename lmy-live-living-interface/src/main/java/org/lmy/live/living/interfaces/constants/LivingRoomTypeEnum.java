package org.lmy.live.living.interfaces.constants;

public enum LivingRoomTypeEnum {
    DEFAULT_LIVING_ROOM(1, "普通直播间"),
    PK_LIVING_ROOM(2, "PK直播间");

    LivingRoomTypeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    int code;
    String desc;

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
