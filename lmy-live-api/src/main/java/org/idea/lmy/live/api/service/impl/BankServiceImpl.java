package org.idea.lmy.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IBankService;
import org.idea.lmy.live.api.vo.req.PayProductReqVO;
import org.idea.lmy.live.api.vo.resp.PayProductItemVO;
import org.idea.lmy.live.api.vo.resp.PayProductRespVO;
import org.idea.lmy.live.api.vo.resp.PayProductVO;
import org.lmy.live.bank.interfaces.constants.OrderStatusEnum;
import org.lmy.live.bank.interfaces.constants.PaySourceEnum;
import org.lmy.live.bank.interfaces.dto.LmyCurrencyAccountDTO;
import org.lmy.live.bank.interfaces.dto.PayOrderDTO;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.interfaces.rpc.ILmyCurrencyAccountRpc;
import org.lmy.live.bank.interfaces.rpc.IPayOrderRpc;
import org.lmy.live.bank.interfaces.rpc.IPayProductRpc;
import org.lmy.live.web.starter.context.LmyRequestContext;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class BankServiceImpl implements IBankService {

    private static final Logger logger= LoggerFactory.getLogger(BankServiceImpl.class);
    @DubboReference
    private IPayProductRpc payProductRpc;

    @Resource
    private RestTemplate restTemplate;

    @DubboReference
    private ILmyCurrencyAccountRpc currencyAccountRpc;

    @DubboReference
    private IPayOrderRpc payOrderRpc;

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
        ErrorAssert.isTure(payProductReqVO.getProductId()!=null&&payProductReqVO.getPaySource()!=null,BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()),BizBaseErrorEnum.PARAM_ERROR);
        PayProductDTO productById = payProductRpc.getProductById(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(productById,BizBaseErrorEnum.PARAM_ERROR);

        //插入一条订单，待支付状态
        PayOrderDTO payOrderDTO=new PayOrderDTO();
        payOrderDTO.setUserId(LmyRequestContext.getUserId());
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);
        //更新订单为支付中状态
        PayOrderDTO updateDTO=new PayOrderDTO();
        updateDTO.setStatus(OrderStatusEnum.PAYING.getCode());
        updateDTO.setOrderId(orderId);
        payOrderRpc.updateOrder(updateDTO);
        PayProductRespVO payProductRespVO=new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);

        //todo 远程http请求 resttemplate-》支付回调接口
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("orderId", orderId);
        jsonObject.put("userId", LmyRequestContext.getUserId());
        jsonObject.put("bizCode", 10001);
        HashMap<String,String> paramMap = new HashMap<>();
        paramMap.put("param",jsonObject.toJSONString());
        ResponseEntity<String> resultEntity = restTemplate.postForEntity("http://localhost:8071/live/api/payNotify/wxNotify?param={param}", null, String.class,paramMap);
        logger.info("[BankServiceImpl] payProduct resultEntity is {}",resultEntity);
        return payProductRespVO;
    }
}
