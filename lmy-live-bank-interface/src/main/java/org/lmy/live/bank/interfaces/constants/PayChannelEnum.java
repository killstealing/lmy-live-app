package org.lmy.live.bank.interfaces.constants;

/**
 * 支付渠道（0支付宝 1微信 2银联 3收银台）
 */
public enum PayChannelEnum {
    ZHI_FU_BAO(1,"支付宝"),
    WEI_XIN(2,"微信"),
    YIN_LIAN(3,"银联"),
    SHOU_YIN_TAI(4,"收银台");

    PayChannelEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    int code;
    String desc;

    public int getCode() {
        return code;
    }
}
