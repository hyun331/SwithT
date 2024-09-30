package com.tweety.SwithT.common.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        System.out.println("여기오냐?");

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        String bearerToken = request.getHeader("Authorization");

        String path = request.getRequestURI();
        System.out.println(path + " 여기에 path");
        System.out.println(request.getRequestURI());
        System.out.println(request.getAuthType());
        System.out.println(request.getMethod());
        // 구글 로그인 관련 요청을 제외
        if (path.startsWith("/member-service/oauth2/authorization/google") || path.startsWith("/member-service/login/oauth2/code/google")) {
            filterChain.doFilter(servletRequest, servletResponse);
            return; // 필터를 여기서 종료
        }

        // 1. 로그로 Authorization 헤더 확인
//        log.info("Authorization Header: {}", bearerToken);

        System.out.println("여기는?");
        try {
            System.out.println("여기는? try");
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {

                System.out.println("여기는 if");
                String token = bearerToken.substring(7);
                Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

                // 2. 로그로 토큰에서 추출된 정보 확인
//                log.info("Claims: {}", claims);

                // 토큰에서 권한 정보 추출 및 설정
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));

                UserDetails userDetails = new User(claims.getSubject(), "", authorities);
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, bearerToken, userDetails.getAuthorities());

                // 3. 로그로 인증 정보 확인
//                log.info("Authentication: {}", authentication);

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } else {
                System.out.println("여기는 무조건오겟지");
                log.warn("JWT Token is missing or does not start with Bearer");
            }

            filterChain.doFilter(servletRequest, servletResponse);

        } catch (SecurityException e) {
            log.error(e.getMessage());
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("token error");
        }
    }
}