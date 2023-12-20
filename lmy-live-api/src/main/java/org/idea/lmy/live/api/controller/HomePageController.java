package org.idea.lmy.live.api.controller;

import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.web.starter.LmyRequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomePageController {

    @PostMapping("/initPage")
    public WebResponseVO initPage(){
        Long userId = LmyRequestContext.getUserId();
        System.out.println(userId);
        return WebResponseVO.success();
    }
}
