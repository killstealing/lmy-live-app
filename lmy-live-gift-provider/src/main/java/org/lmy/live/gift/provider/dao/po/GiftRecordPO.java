package org.lmy.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_gift_record")
public class GiftRecordPO {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long objectId;
    private Integer giftId;
    private Integer price;
    private Integer priceUnit;
    private Integer source;
    private Date sendTime;
    private Date updateTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public Integer getGiftId() {
        return giftId;
    }

    public void setGiftId(Integer giftId) {
        this.giftId = giftId;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getPriceUnit() {
        return priceUnit;
    }

    public void setPriceUnit(Integer priceUnit) {
        this.priceUnit = priceUnit;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Date getSendTime() {
        return sendTime;
    }

    public void setSendTime(Date sendTime) {
        this.sendTime = sendTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "GiftRecordPO{" +
                "id=" + id +
                ", userId=" + userId +
                ", objectId=" + objectId +
                ", giftId=" + giftId +
                ", price=" + price +
                ", priceUnit=" + priceUnit +
                ", source=" + source +
                ", sendTime=" + sendTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
