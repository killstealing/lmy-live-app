package org.idea.lmy.live.api.vo.req;

public class GiftReqVO {
    private int giftId;
    private Integer roomId;
    private Long senderUserId;
    private Long receiverId;

    public int getGiftId() {
        return giftId;
    }

    public void setGiftId(int giftId) {
        this.giftId = giftId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Long getSenderUserId() {
        return senderUserId;
    }

    public void setSenderUserId(Long senderUserId) {
        this.senderUserId = senderUserId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    @Override
    public String toString() {
        return "GiftReqVO{" +
                "giftId=" + giftId +
                ", roomId=" + roomId +
                ", senderUserId=" + senderUserId +
                ", receiverId=" + receiverId +
                '}';
    }
}
