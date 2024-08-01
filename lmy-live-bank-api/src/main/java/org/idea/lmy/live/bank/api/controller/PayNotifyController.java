package org.idea.lmy.live.bank.api.controller;


import jakarta.annotation.Resource;
import org.idea.lmy.live.bank.api.service.IPayNotifyService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {

    @Resource
    private IPayNotifyService payNotifyService;

    @PostMapping("/wx-notify")
    public String weixinPayNotify(@RequestParam("param") String param){
        String result = payNotifyService.notifyHandler(param);
        return result;
    }

}
