package org.lmy.live.bank.interfaces.constants;

/**
 * 状态（0待支付，1支付中，2已支付，3撤销，4无效）
 */
public enum OrderStatusEnum {
    DAI_PAY(0,"待支付"),
    PAYING(1,"支付中"),
    PAID(2,"已支付"),
    CANCEL(3,"撤销"),
    IN_VALID(4,"无效");

    int code;
    String desc;

    OrderStatusEnum(int code, String desc) {
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
