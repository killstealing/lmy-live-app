package org.idea.lmy.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IBankService;
import org.idea.lmy.live.api.vo.req.PayProductReqVO;
import org.idea.lmy.live.api.vo.resp.PayProductItemVO;
import org.idea.lmy.live.api.vo.resp.PayProductRespVO;
import org.idea.lmy.live.api.vo.resp.PayProductVO;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.bank.interfaces.rpc.IPayProductRpc;
import org.lmy.live.common.interfaces.enums.PaySourceEnum;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
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

    @Override
    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        ErrorAssert.isNotNull(payProductReqVO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isTure(payProductReqVO.getProductId()!=null&&payProductReqVO.getProductId()!=null,BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()),BizBaseErrorEnum.PARAM_ERROR);
        PayProductDTO productById = payProductRpc.getProductById(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(productById,BizBaseErrorEnum.PARAM_ERROR);

        //插入一条订单，待支付状态

        //更新订单为支付中状态

        return null;
    }
}
