package org.lmy.live.gift.interfaces.dto;

/**
 * @Author idea
 * @Date: Created in 16:49 2023/9/9
 * @Description
 */
public class RedPacketReceiveDTO{
    private Integer price;
    private String notifyMsg;

    public RedPacketReceiveDTO(Integer price) {
        this.price = price;
    }

    public RedPacketReceiveDTO(Integer price,String notifyMsg) {
        this.price = price;
        this.notifyMsg = notifyMsg;
    }

    public String getNotifyMsg() {
        return notifyMsg;
    }

    public void setNotifyMsg(String notifyMsg) {
        this.notifyMsg = notifyMsg;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

}
