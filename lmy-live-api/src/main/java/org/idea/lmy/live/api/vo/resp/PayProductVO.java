package org.idea.lmy.live.api.vo.resp;

import java.util.List;

public class PayProductVO {
    private Integer currentBalance;
    private List<PayProductItemVO> payProductItemVOList;

    public Integer getCurrentBalance() {
        return currentBalance;
    }

    public void setCurrentBalance(Integer currentBalance) {
        this.currentBalance = currentBalance;
    }

    public List<PayProductItemVO> getPayProductItemVOList() {
        return payProductItemVOList;
    }

    public void setPayProductItemVOList(List<PayProductItemVO> payProductItemVOList) {
        this.payProductItemVOList = payProductItemVOList;
    }
}
