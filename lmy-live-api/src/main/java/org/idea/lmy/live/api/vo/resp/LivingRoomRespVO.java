package org.idea.lmy.live.api.vo.resp;

public class LivingRoomRespVO {
    private Integer id;
    private String roomName;
    private Long anchorId;
    private Integer watchNum;
    private Integer goodNum;
    private String covertImg;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Integer getWatchNum() {
        return watchNum;
    }

    public void setWatchNum(Integer watchNum) {
        this.watchNum = watchNum;
    }

    public Integer getGoodNum() {
        return goodNum;
    }

    public void setGoodNum(Integer goodNum) {
        this.goodNum = goodNum;
    }


    @Override
    public String toString() {
        return "LivingRoomRespVO{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", anchorId=" + anchorId +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                ", covertImg='" + covertImg + '\'' +
                '}';
    }
}
