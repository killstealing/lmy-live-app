package org.lmy.live.bank.interfaces.rpc;

import org.lmy.live.bank.interfaces.dto.PayProductDTO;

import java.util.List;

public interface IPayProductRpc {
    List<PayProductDTO> getProductList(Integer type);
}
