package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import org.idea.lmy.live.api.service.IHomePageService;
import org.idea.lmy.live.api.vo.HomePageVO;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.lmy.live.web.starter.LmyRequestContext;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomePageController {

    @Resource
    private IHomePageService iHomePageService;

    @PostMapping("/initPage")
    public WebResponseVO initPage(){
        Long userId = LmyRequestContext.getUserId();
        HomePageVO homePageVO=new HomePageVO();
        homePageVO.setLoginStatus(false);
        if(userId!=null){
            homePageVO = iHomePageService.initPage(userId);
            homePageVO.setLoginStatus(true);
        }
        return WebResponseVO.success(homePageVO );
    }
}
