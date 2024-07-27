package org.lmy.live.web.starter.config;

import org.lmy.live.web.starter.context.LmyUserInfoInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public LmyUserInfoInterceptor lmyUserInfoInterceptor(){
        return new LmyUserInfoInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(lmyUserInfoInterceptor()).addPathPatterns("/**").excludePathPatterns("/error");
    }
}
