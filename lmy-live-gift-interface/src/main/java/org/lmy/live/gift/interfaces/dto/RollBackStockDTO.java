package org.lmy.live.gift.interfaces.dto;

import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 08:23 2023/10/20
 * @Description
 */
public class RollBackStockDTO implements Serializable {

    private Long orderId;
    private Long userId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
