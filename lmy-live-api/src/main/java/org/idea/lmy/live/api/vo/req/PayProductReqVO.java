package org.idea.lmy.live.api.vo.req;

public class PayProductReqVO {
    /**
     * 产品id
     */
    private Integer productId;

    /**
     * 支付渠道 (直播间，个人中心，聊天页面，第三方宣传页面，广告弹窗引导)
     * @see PaySourceEnum
     */
    private Integer paySource;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getPaySource() {
        return paySource;
    }

    public void setPaySource(Integer paySource) {
        this.paySource = paySource;
    }

    @Override
    public String toString() {
        return "PayProductReqVO{" +
                "productId=" + productId +
                ", paySource=" + paySource +
                '}';
    }
}
