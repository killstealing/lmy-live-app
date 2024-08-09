package org.lmy.live.gift.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 11:26 2023/10/22
 * @Description
 */
public class SkuPrepareOrderInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4225814764392434579L;
    private List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS;
    private Integer totalPrice;

    public List<SkuPrepareOrderItemInfoDTO> getSkuPrepareOrderItemInfoDTOS() {
        return skuPrepareOrderItemInfoDTOS;
    }

    public void setSkuPrepareOrderItemInfoDTOS(List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS) {
        this.skuPrepareOrderItemInfoDTOS = skuPrepareOrderItemInfoDTOS;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
