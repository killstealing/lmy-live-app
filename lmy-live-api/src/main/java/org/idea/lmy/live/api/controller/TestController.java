package org.idea.lmy.live.api.controller;

import org.lmy.live.web.starter.config.RequestLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    private static final Logger logger= LoggerFactory.getLogger(TestController.class);

    @RequestLimit(limit = 1,second = 10)
    @PostMapping("/testPost1")
    public String testPost1(String id){
        logger.info("[testPost1] id is{}",id);
        return "testPost1";
    }

    @PostMapping("/testPost2")
    public String testPost2(String id){
        logger.info("[testPost2] id is{}",id);
        return "testPost2";
    }
}
