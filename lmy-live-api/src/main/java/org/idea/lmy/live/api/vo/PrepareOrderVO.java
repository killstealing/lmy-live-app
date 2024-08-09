package org.idea.lmy.live.api.vo;

/**
 * @Author idea
 * @Date: Created in 22:44 2023/10/16
 * @Description
 */
public class PrepareOrderVO {

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
