package org.lmy.live.gift.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 16:39 2023/10/4
 * @Description 商品购物车数据展示
 */
public class ShopCarRespDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -2333856134764035912L;

    private Long userId;
    private Integer roomId;
    private List<SkuInfoDTO> skuInfoDTOList;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public List<SkuInfoDTO> getSkuInfoDTOList() {
        return skuInfoDTOList;
    }

    public void setSkuInfoDTOList(List<SkuInfoDTO> skuInfoDTOList) {
        this.skuInfoDTOList = skuInfoDTOList;
    }
}
