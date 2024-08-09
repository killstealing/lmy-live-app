package org.lmy.live.gift.interfaces.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @Author idea
 * @Date: Created in 07:25 2023/10/16
 * @Description
 */
public class SkuOrderInfoReqDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = -4339322704298737807L;

    private Long id;
    private Long userId;
    private Integer roomId;
    private Integer status;
    private List<Long> skuIdList;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

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

    public List<Long> getSkuIdList() {
        return skuIdList;
    }

    public void setSkuIdList(List<Long> skuIdList) {
        this.skuIdList = skuIdList;
    }
}
