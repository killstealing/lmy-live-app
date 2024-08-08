package org.lmy.live.im.router.interfaces.constants;

public enum ImMsgBizCodeEnum {
    LIVING_ROOM_IM_CHAT_MSG_BIZ(5555, "直播间im聊天消息"),
    LIVING_ROOM_SEND_GIFT_SUCCESS(5556,"送礼成功"),
    LIVING_ROOM_SEND_GIFT_FAIL(5557,"送礼失败"),
    LIVING_ROOM_PK_SEND_GIFT_SUCCESS(5558,"pk送礼成功"),
    LIVING_ROOM_PK_ONLINE(5559,"pk连线"),
    START_RED_PACKET(5560,"开启红包雨活动");

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
