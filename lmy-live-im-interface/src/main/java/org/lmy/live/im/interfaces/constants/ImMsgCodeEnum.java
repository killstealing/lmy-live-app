package org.lmy.live.im.interfaces.constants;

public enum ImMsgCodeEnum {
    IM_LOGIN_MSG(1001,"登录im消息包"),
    IM_LOGOUT_MSG(1002,"登出IM消息包"),
    IM_BIZ_MSG(1003,"业务IM消息包"),
    IM_HEART_BEAT_MSG(1004,"心跳IM消息包"),
    IM_ACK_MSG(1005,"IM服务ACK消息包");

    int code;
    String desc;

    ImMsgCodeEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
