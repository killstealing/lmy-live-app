package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.req.PayProductReqVO;
import org.idea.lmy.live.api.vo.resp.PayProductRespVO;
import org.idea.lmy.live.api.vo.resp.PayProductVO;

public interface IBankService {
    PayProductVO getProductList(Integer type);
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);
}
