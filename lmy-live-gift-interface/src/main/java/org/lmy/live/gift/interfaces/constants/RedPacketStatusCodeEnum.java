package org.lmy.live.gift.interfaces.constants;

/**
 * @Author idea
 * @Date: Created in 11:00 2023/9/23
 * @Description
 */
public enum RedPacketStatusCodeEnum {

    NOT_PREPARE(1,"待准备"),
    IS_PREPARE(2,"已准备"),
    HAS_SEND(3,"已发送");

    int code;
    String desc;

    RedPacketStatusCodeEnum(int code, String desc) {
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
