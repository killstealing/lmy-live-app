package org.idea.lmy.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IBankService;
import org.idea.lmy.live.api.vo.resp.PayProductItemVO;
import org.idea.lmy.live.api.vo.resp.PayProductVO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.bank.interfaces.rpc.IPayProductRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements IBankService {

    @DubboReference
    private IPayProductRpc payProductRpc;

    @DubboReference
    private ILmyCurrencyAccountRpc currencyAccountRpc;

    @Override
    public PayProductVO getProductList(Integer type) {
        List<PayProductDTO> productList = payProductRpc.getProductList(type);
        List<PayProductItemVO> resultList=new ArrayList<>();
        productList.forEach(payProductDTO -> {
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            PayProductItemVO payProductItemVO =new PayProductItemVO();
            payProductItemVO.setId(payProductDTO.getId());
            payProductItemVO.setName(payProductDTO.getName());
            payProductItemVO.setCoinNum(jsonObject.getInteger("coin"));
            resultList.add(payProductItemVO);
        });
        LmyCurrencyAccountDTO accountDTO = currencyAccountRpc.getByUserId(LmyRequestContext.getUserId());
        PayProductVO payProductVO=new PayProductVO();
        payProductVO.setPayProductItemVOList(resultList);
        payProductVO.setCurrentBalance(accountDTO.getCurrentBalance());
        return payProductVO;
    }
}
