package org.idea.lmy.live.bank.api.controller;


import jakarta.annotation.Resource;
import org.idea.lmy.live.bank.api.service.IPayNotifyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payNotify")
public class PayNotifyController {
    private static final Logger logger= LoggerFactory.getLogger(PayNotifyController.class);
    @Resource
    private IPayNotifyService payNotifyService;

    @PostMapping("/wxNotify")
    public String weixinPayNotify(@RequestParam("param") String param){
        String result = payNotifyService.notifyHandler(param);
        return result;
    }

    @GetMapping("/test")
    public String test(){
        logger.info("test setest setest");
        return "test111";
    }

}
