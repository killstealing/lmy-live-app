package org.idea.lmy.live.api.vo.req;

public class LivingRoomReqVO {

    private Integer type;
    private int page;
    private int pageSize;
    private Integer roomId;
    private String redPacketConfigCode;

    public String getRedPacketConfigCode() {
        return redPacketConfigCode;
    }

    public void setRedPacketConfigCode(String redPacketConfigCode) {
        this.redPacketConfigCode = redPacketConfigCode;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "LivingRoomReqVO{" +
                "type=" + type +
                ", page=" + page +
                ", roomId=" + roomId +
                ", pageSize=" + pageSize +
                '}';
    }
}
