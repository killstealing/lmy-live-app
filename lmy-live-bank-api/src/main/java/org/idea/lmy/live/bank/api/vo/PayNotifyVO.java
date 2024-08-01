package org.idea.lmy.live.bank.api.vo;

public class PayNotifyVO {
    private Long userId;
    private String orderId;
    private Integer bizCode;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Integer getBizCode() {
        return bizCode;
    }

    public void setBizCode(Integer bizCode) {
        this.bizCode = bizCode;
    }

    @Override
    public String toString() {
        return "PayNotifyVO{" +
                "userId=" + userId +
                ", orderId='" + orderId + '\'' +
                ", bizCode=" + bizCode +
                '}';
    }
}
