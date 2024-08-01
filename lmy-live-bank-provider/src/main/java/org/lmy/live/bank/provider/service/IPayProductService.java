package org.lmy.live.bank.provider.service;

import org.lmy.live.bank.interfaces.dto.PayProductDTO;

import java.util.List;

public interface IPayProductService {
    List<PayProductDTO> getProductList(Integer type);
}
