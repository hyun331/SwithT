package com.tweety.SwithT.common.configs;

import com.tweety.SwithT.common.auth.JwtAuthFilter;
import com.tweety.SwithT.member.service.CustomOAuth2UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigs {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    public SecurityConfigs(CustomOAuth2UserService customOAuth2UserService) {
        this.customOAuth2UserService = customOAuth2UserService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        System.out.println("여기는 요청 옴? 여긴 시큐리티 컨피그");
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configure(httpSecurity))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
                        .successHandler((request, response, authentication) -> {
                            // JWT 토큰 발급 또는 사용자 처리 로직
                            System.out.println("Login Success: " + authentication.getName());
                            response.getWriter().write("Login Success!");  // 또는 리디렉션
                        })
                )// CORS 활성화
                .httpBasic(httpBasic -> httpBasic.disable()) // 기본 인증 비활성화
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("**").permitAll()
                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
