package org.lmy.live.gift.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 16:49 2023/9/9
 * @Description
 */
public class RedPacketReceiveDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 9059575343595910533L;

    private Integer price;

    public RedPacketReceiveDTO(Integer price) {
        this.price = price;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "RedPacketReceiveDTO{" +
                "price=" + price +
                '}';
    }
}
