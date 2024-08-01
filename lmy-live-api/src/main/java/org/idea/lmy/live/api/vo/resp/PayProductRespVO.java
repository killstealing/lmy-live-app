package org.idea.lmy.live.api.vo.resp;

public class PayProductRespVO {
    private String orderId;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public String toString() {
        return "PayProductRespVO{" +
                "orderId='" + orderId + '\'' +
                '}';
    }
}
