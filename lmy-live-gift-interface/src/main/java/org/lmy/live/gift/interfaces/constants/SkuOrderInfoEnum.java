package org.lmy.live.gift.interfaces.constants;

/**
 * @Author idea
 * @Date: Created in 22:53 2023/10/16
 * @Description 商品订单信息枚举
 */
public enum SkuOrderInfoEnum {

    PREPARE_PAY(0,"待支付状态"),
    HAS_PAY(1,"已支付状态"),
    END(2,"订单已关闭");

    Integer code;
    String desc;

    SkuOrderInfoEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public Integer getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
