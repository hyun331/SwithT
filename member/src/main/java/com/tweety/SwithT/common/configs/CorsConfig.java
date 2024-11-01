package com.tweety.SwithT.common.configs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                .allowedOrigins("https://www.switht.co.kr", "http://localhost:8081")
                .allowedMethods("*")
                .allowedHeaders("*")
                .allowCredentials(true);

//                 임시 주석 처리 11_01 김민성
//                "https://server.switht.co.kr",
//                "https://server.switht.co.kr/member-service/login/oauth2/code/google",
//                "https://server.switht.co.kr/member-service/login/oauth2/code/kakao",
//                "https://server.switht.co.kr/member-service/oauth2/authorization/google",
//                "https://server.switht.co.kr/member-service/oauth2/authorization/kakao"
        
    }
}
