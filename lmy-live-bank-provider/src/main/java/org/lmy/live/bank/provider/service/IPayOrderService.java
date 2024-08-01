package org.lmy.live.bank.provider.service;

import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.provider.dao.po.PayOrderPO;

public interface IPayOrderService {
    String insertOne(PayOrderDTO payOrderDTO);
    boolean updateOrder(PayOrderDTO payOrderDTO);
    /**
     * 根据订单id查询
     *
     * @param orderId
     */
    PayOrderPO queryByOrderId(String orderId);
    /**
     * 支付回调需要请求该接口
     *
     * @param payOrderDTO
     * @return
     */
    boolean payNotify(PayOrderDTO payOrderDTO);
}
