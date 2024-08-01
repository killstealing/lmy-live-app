package org.lmy.live.bank.provider.service;

import org.lmy.live.bank.interfaces.dto.PayOrderDTO;

public interface IPayOrderService {
    String insertOne(PayOrderDTO payOrderDTO);
    boolean updateOrder(PayOrderDTO payOrderDTO);
}
