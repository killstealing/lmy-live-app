package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.IBankService;
import org.idea.lmy.live.api.vo.resp.PayProductVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.web.starter.error.BizBaseErrorEnum;
import org.lmy.live.web.starter.error.ErrorAssert;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/bank")
public class BankController {

    @Resource
    private IBankService bankService;

    @GetMapping("/products")
    public WebResponseVO products(Integer type){
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        List<PayProductVO> productList = bankService.getProductList(type);
        return WebResponseVO.success(productList);
    }

}
