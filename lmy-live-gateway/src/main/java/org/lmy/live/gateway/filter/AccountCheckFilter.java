package org.lmy.live.gateway.filter;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.lmy.live.account.interfaces.IAccountTokenRPC;
import org.lmy.live.common.interfaces.enums.GatewayHeaderEnum;
import org.lmy.live.gateway.properties.GatewayApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

import static io.netty.handler.codec.http.cookie.CookieHeaderNames.MAX_AGE;
import static org.springframework.web.cors.CorsConfiguration.ALL;

@Component
public class AccountCheckFilter implements GlobalFilter, Ordered {
    private static final Logger logger= LoggerFactory.getLogger(AccountCheckFilter.class);
    @Resource
    private GatewayApplicationProperties gatewayApplicationProperties;
    @DubboReference
    private IAccountTokenRPC accountTokenRPC;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //request url 是否为空
        ServerHttpRequest request = exchange.getRequest();

        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN, "http://app.lmy.live.com:8080");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        headers.add(HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS, "*");
        headers.add(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, ALL);
        headers.add(HttpHeaders.ACCESS_CONTROL_MAX_AGE, MAX_AGE);


        String path = request.getURI().getPath();
        if(StringUtils.isEmpty(path)){
            return Mono.empty();
        }
        List<String> notCheckUrlList = gatewayApplicationProperties.getNotCheckUrlList();
        for (String uri:notCheckUrlList) {
            if(path.startsWith(uri)){
                logger.error("请求的uri在白名单里面，没有进行token校验，直接传达给业务下游");
                return chain.filter(exchange);
            }
        }
        // 没在白名单， 拿cookie
        List<HttpCookie> lmytk = request.getCookies().get("lmytk");
        if(CollectionUtils.isEmpty(lmytk)){
            //cookie 为空， 返回空
            logger.error("请求的cookie中的lmytk是空，被拦截");
            return Mono.empty();
        }
        String cookieValue = lmytk.get(0).getValue();
        if(StringUtils.isEmpty(cookieValue)||StringUtils.isEmpty(cookieValue.trim())){
            logger.error("请求的cookie中的lmytk是空，被拦截");
            return Mono.empty();
        }
        Long userIdByToken = accountTokenRPC.getUserIdByToken(cookieValue);
        if(userIdByToken==null){
            logger.error("请求的token失效了，被拦截");
            return Mono.empty();
        }
        ServerHttpRequest.Builder mutate = request.mutate();
        mutate.header(GatewayHeaderEnum.USER_LOGIN_ID.getName(), String.valueOf(userIdByToken));
        return chain.filter(exchange.mutate().request(mutate.build()).build());
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
