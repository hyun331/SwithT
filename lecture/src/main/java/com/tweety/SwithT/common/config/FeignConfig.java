package com.tweety.SwithT.common.config;

import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return request -> {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if(authentication != null){
                String token = (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
                if(token!= null){
                    System.out.println(token+"\n\nthis is feign config");
                    request.header(HttpHeaders.AUTHORIZATION, token);
                }else{
                    System.out.println("token null");
                }
            }else{
                System.out.println("securityContext null");
            }

        };
    }
}