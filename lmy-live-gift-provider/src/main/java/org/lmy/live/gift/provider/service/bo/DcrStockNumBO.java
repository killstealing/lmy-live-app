package org.lmy.live.gift.provider.service.bo;

/**
 * @Author idea
 * @Date: Created in 09:14 2023/10/5
 * @Description
 */
public class DcrStockNumBO {

    private boolean isSuccess;
    private boolean noStock;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isNoStock() {
        return noStock;
    }

    public void setNoStock(boolean noStock) {
        this.noStock = noStock;
    }
}
