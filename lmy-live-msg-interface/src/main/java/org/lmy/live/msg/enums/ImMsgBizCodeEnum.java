package org.lmy.live.msg.enums;

public enum ImMsgBizCodeEnum {
    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555, "直播间im聊天消息");

    ImMsgBizCodeEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }

    int code;
    String description;

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
