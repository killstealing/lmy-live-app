package org.lmy.live.gift.provider.service.bo;

import org.lmy.live.gift.interfaces.dto.RedPacketConfigReqDTO;

/**
 * @Author idea
 * @Date: Created in 16:55 2023/9/23
 * @Description
 */
public class SendRedPacketBO {

    private Integer price;
    private RedPacketConfigReqDTO reqDTO;

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public RedPacketConfigReqDTO getReqDTO() {
        return reqDTO;
    }

    public void setReqDTO(RedPacketConfigReqDTO reqDTO) {
        this.reqDTO = reqDTO;
    }
}
