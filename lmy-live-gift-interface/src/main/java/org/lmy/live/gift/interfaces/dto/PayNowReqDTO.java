package org.lmy.live.gift.interfaces.dto;

public class PayNowReqDTO {
    private Long userId;
    private Integer roomId;

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
}
