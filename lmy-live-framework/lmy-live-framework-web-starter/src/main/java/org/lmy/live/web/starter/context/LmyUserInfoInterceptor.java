package org.lmy.live.web.starter.context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lmy.live.common.interfaces.enums.GatewayHeaderEnum;
import org.lmy.live.web.starter.constants.RequestConstants;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LmyUserInfoInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userIdStr=request.getHeader(GatewayHeaderEnum.USER_LOGIN_ID.getName());
        if(StringUtils.isEmpty(userIdStr)){
            return true;
        }
        LmyRequestContext.set(RequestConstants.LMY_USER_ID,Long.valueOf(userIdStr));
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        LmyRequestContext.clear();
    }
}
