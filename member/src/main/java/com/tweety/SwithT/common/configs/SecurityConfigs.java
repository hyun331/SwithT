package com.tweety.SwithT.common.configs;

import com.tweety.SwithT.common.auth.JwtAuthFilter;
import com.tweety.SwithT.member.handler.CustomSuccessHandler;
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
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomSuccessHandler customSuccessHandler;

    @Autowired
    public SecurityConfigs(CustomOAuth2UserService customOAuth2UserService, CustomSuccessHandler customSuccessHandler) {
        this.customOAuth2UserService = customOAuth2UserService;
        this.customSuccessHandler = customSuccessHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(csrf -> csrf.disable()) // CSRF 비활성화
                .cors(cors -> cors.configure(httpSecurity))
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpoint -> userInfoEndpoint
                                .userService(customOAuth2UserService))
                        .successHandler(customSuccessHandler)
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
