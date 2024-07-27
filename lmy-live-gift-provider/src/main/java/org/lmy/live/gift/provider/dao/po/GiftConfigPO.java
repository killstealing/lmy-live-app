package org.lmy.live.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_gift_config")
public class GiftConfigPO {
    @TableId(type = IdType.AUTO)
    private Integer giftId;
    private Integer price;
    private String giftName;
    private Integer status;
    private String coverImgUrl;
    private String svgaUrl;
    private Date createTime;
    private Date updateTime;

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

    public String getGiftName() {
        return giftName;
    }

    public void setGiftName(String giftName) {
        this.giftName = giftName;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getSvgaUrl() {
        return svgaUrl;
    }

    public void setSvgaUrl(String svgaUrl) {
        this.svgaUrl = svgaUrl;
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
        return "GiftConfigPO{" +
                "giftId=" + giftId +
                ", price=" + price +
                ", giftName='" + giftName + '\'' +
                ", status=" + status +
                ", coverImgUrl='" + coverImgUrl + '\'' +
                ", svgaUrl='" + svgaUrl + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
