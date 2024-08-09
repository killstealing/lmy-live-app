package org.lmy.live.gift.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;

/**
 * @Author idea
 * @Date: Created in 11:27 2023/10/22
 * @Description
 */
public class SkuPrepareOrderItemInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 6033305737226285944L;
    private SkuInfoDTO skuInfoDTO;
    private Integer count;

    public SkuInfoDTO getSkuInfoDTO() {
        return skuInfoDTO;
    }

    public void setSkuInfoDTO(SkuInfoDTO skuInfoDTO) {
        this.skuInfoDTO = skuInfoDTO;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
