package org.lmy.live.bank.interfaces.rpc;

import org.lmy.live.bank.interfaces.dto.PayOrderDTO;

public interface IPayOrderRpc {
    String insertOne(PayOrderDTO payOrderDTO);
    boolean updateOrder(PayOrderDTO payOrderDTO);
}
