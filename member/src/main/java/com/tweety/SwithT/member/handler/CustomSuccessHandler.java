package com.tweety.SwithT.member.handler;


import com.tweety.SwithT.common.auth.JwtTokenProvider;
import com.tweety.SwithT.common.service.RedisService;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final String REDIRECT_URL = "http://localhost:8082//mypage";
    private final RedisService redisService;

    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository, RedisService redisService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
        this.redisService = redisService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        // 인증된 사용자 정보를 가져옵니다.
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 회원 정보를 데이터베이스에서 조회합니다.
        Optional<Member> member = memberRepository.findByEmail(email);

        // 회원이 존재하는지 확인하고, 정보 추출
        if (member.isPresent()) {
            String name = member.get().getName();
            Long memberId = member.get().getId();

            // JWT 토큰 생성
            String jwtToken = jwtTokenProvider.createToken(String.valueOf(memberId), email, String.valueOf(member.get().getRole()), name);

            System.out.println(jwtToken);
            System.out.println(memberId);
            // 리디렉션 URL에 토큰과 회원 정보를 쿼리 파라미터로 추가
            String redirectUrl = REDIRECT_URL + "?token=" + jwtToken + "&memberId=" + memberId;

            // 리디렉션
            response.sendRedirect(redirectUrl);
        } else {
            // 회원 정보가 없을 경우의 처리 (예: 에러 응답)
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "User not found");
        }
    }


//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException{
//        // OAuth2User로 캐스팅하여 인증된 사용자 정보를 가져온다.
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        //디버깅.
//        System.out.println("Attributes: " + oAuth2User.getAttributes());
//        String email = oAuth2User.getAttribute("email").toString();
//        String provider = oAuth2User.getAttribute("provider");
//        String name = oAuth2User.getAttribute("name");
//
////        System.out.println("핸들러!!!!!!!");
////        System.out.println(email);
////        System.out.println(provider);
////        System.out.println(name);
//
//        response.sendRedirect(REDIRECT_URL);
//
//    }
}
