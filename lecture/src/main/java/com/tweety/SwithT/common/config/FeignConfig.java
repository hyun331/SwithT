package com.tweety.SwithT.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return request -> {
            String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
            System.out.println("your token: " + token);
            request.header(HttpHeaders.AUTHORIZATION, token);
        };
    }
}