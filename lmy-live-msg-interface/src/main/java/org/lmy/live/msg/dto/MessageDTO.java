package org.lmy.live.msg.dto;

import java.io.Serializable;
import java.util.Date;

/**
 * 发送消息的内容
 */
public class MessageDTO implements Serializable {

    private Long userId;
    private Integer roomId;
    private String senderName;
    private String senderAvtar;
    /**
     * 消息类型
     */
    private Integer type;
    /**
     * 消息内容
     */
    private String content;
    private Date createTime;
    private Date updateTime;

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderAvtar() {
        return senderAvtar;
    }

    public void setSenderAvtar(String senderAvtar) {
        this.senderAvtar = senderAvtar;
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

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "MessageDTO{" +
                "userId=" + userId +
                ", roomId=" + roomId +
                ", senderName='" + senderName + '\'' +
                ", senderAvtar='" + senderAvtar + '\'' +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
