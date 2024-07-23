package org.lmy.live.living.interfaces.dto;

import java.io.Serializable;

public class LivingRoomRespDTO implements Serializable {

    private Integer id;
    private Long anchorId;
    private String roomName;
    private Integer roomId;
    private String covertImg;
    private Integer type;
    private Integer watchNum;
    private Integer goodNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public String getCovertImg() {
        return covertImg;
    }

    public void setCovertImg(String covertImg) {
        this.covertImg = covertImg;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
        return "LivingRoomRespDTO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", roomName='" + roomName + '\'' +
                ", roomId=" + roomId +
                ", covertImg='" + covertImg + '\'' +
                ", type=" + type +
                ", watchNum=" + watchNum +
                ", goodNum=" + goodNum +
                '}';
    }
}
