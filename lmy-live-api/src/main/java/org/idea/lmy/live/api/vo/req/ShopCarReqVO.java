package org.idea.lmy.live.api.vo.req;

/**
 * @Author idea
 * @Date: Created in 16:24 2023/10/4
 * @Description
 */
public class ShopCarReqVO {

    private Long skuId;
    private Integer roomId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
