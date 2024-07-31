package org.lmy.live.bank.interfaces.constants;

/**
 * @Author idea
 * @Date: Created in 21:52 2023/8/7
 * @Description
 */
public enum TradeTypeEnum {

    SEND_GIFT_TRADE(0,"送礼物交易");

    int code;
    String desc;

    TradeTypeEnum(int code, String desc) {
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
