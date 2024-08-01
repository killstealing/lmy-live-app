package org.idea.lmy.live.api.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.dubbo.config.annotation.DubboReference;
import org.idea.lmy.live.api.service.IBankService;
import org.idea.lmy.live.api.vo.resp.PayProductVO;
import org.lmy.live.bank.interfaces.dto.PayProductDTO;
import org.lmy.live.bank.interfaces.rpc.IPayProductRpc;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BankServiceImpl implements IBankService {

    @DubboReference
    private IPayProductRpc payProductRpc;

    @Override
    public List<PayProductVO> getProductList(Integer type) {
        List<PayProductDTO> productList = payProductRpc.getProductList(type);
        List<PayProductVO> resultList=new ArrayList<>();
        productList.forEach(payProductDTO -> {
            JSONObject jsonObject = JSON.parseObject(payProductDTO.getExtra());
            PayProductVO payProductVO=new PayProductVO();
            payProductVO.setId(payProductDTO.getId());
            payProductVO.setName(payProductDTO.getName());
            payProductVO.setCoinNum(jsonObject.getInteger("coin"));
            resultList.add(payProductVO);
        });
        return resultList;
    }
}
