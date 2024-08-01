package org.idea.lmy.live.api.service;

import org.idea.lmy.live.api.vo.resp.PayProductVO;

import java.util.List;

public interface IBankService {
    List<PayProductVO> getProductList(Integer type);
}
