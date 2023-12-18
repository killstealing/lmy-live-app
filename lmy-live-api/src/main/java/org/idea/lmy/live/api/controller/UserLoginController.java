package org.idea.lmy.live.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.idea.lmy.live.api.service.IUserLoginService;
import org.lmy.live.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/userLogin")
public class UserLoginController {
    @Resource
    private IUserLoginService userLoginService;

    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone){
        return userLoginService.sendLoginCode(phone);
    }

    @PostMapping("/login")
    public WebResponseVO login(String phone, Integer code, HttpServletResponse httpServletResponse){
        return userLoginService.login(phone,code,httpServletResponse);
    }


}
