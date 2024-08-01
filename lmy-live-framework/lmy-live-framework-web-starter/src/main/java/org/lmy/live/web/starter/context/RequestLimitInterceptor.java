package org.lmy.live.web.starter.context;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.lmy.live.web.starter.config.RequestLimit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

public class RequestLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger= LoggerFactory.getLogger(RequestLimitInterceptor.class);

    @Value("${spring.application.name}")
    private String applicationName;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(handler instanceof HandlerMethod){
            HandlerMethod handlerMethod= (HandlerMethod) handler;
            boolean hasLimit=handlerMethod.getMethod().isAnnotationPresent(RequestLimit.class);
            if(hasLimit){
                //是否进行拦截
                RequestLimit requestLimit=handlerMethod.getMethodAnnotation(RequestLimit.class);
                Long userId = LmyRequestContext.getUserId();
                userId=12132123L;
                if(userId==null){
                    return true;
                }
                int limit = requestLimit.limit();
                int second = requestLimit.second();
                String requestURI = request.getRequestURI();
                String cacheKey=applicationName+":"+requestURI+":"+userId;
                Integer requestTime = (Integer) Optional.ofNullable(redisTemplate.opsForValue().get(cacheKey)).orElse(0);
                if(requestTime==0){
                    redisTemplate.opsForValue().set(cacheKey,1,second, TimeUnit.SECONDS);
                    return true;
                }else if(requestTime<limit){
                    redisTemplate.opsForValue().increment(cacheKey,1);
                    return true;
                }else{
                    logger.info("[RequestLimitInterceptor] userId is {}, req two muuch,",userId);
                    return false;
                }
            }
        }
        return true;
    }
}
