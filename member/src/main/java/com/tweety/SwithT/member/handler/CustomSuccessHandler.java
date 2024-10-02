package com.tweety.SwithT.member.handler;


import com.tweety.SwithT.common.auth.JwtTokenProvider;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Optional;
/*
프론트엔드 작업하면서 추가정보 입력 테스트를 진행해야합니다!!!!!!!!!!!!!!!!!!!!
프론트엔드 작업하면서 추가정보 입력 테스트를 진행해야합니다!!!!!!!!!!!!!!!!!!!!
프론트엔드 작업하면서 추가정보 입력 테스트를 진행해야합니다!!!!!!!!!!!!!!!!!!!!
프론트 붙이면서 완성하겠습니다!!!!!!!!!!!!!!!!!!!!
 */
@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    private final String REDIRECT_URL = "http://localhost:8082/mypage"; //테스트를 위해서 임시로 뒀음.
    private final String REDIRECT_URL_EXIST = "http://localhost:8082/"; //테스트를 위해서 임시로 뒀음.

    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 소셜 로그인에 성공한 회원 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        // 회원 정보를 DB에서 조회.
        Optional<Member> member = memberRepository.findByEmail(email);

        // 회원이 존재하는지 확인하고, 정보 추출
        if (member.isPresent()) {
            String name = member.get().getName();
            Long memberId = member.get().getId();

            // 회원 정보를 쿠키에 담아서 보내기
            Cookie memberCookie = new Cookie("memberId", String.valueOf(memberId));
            memberCookie.setPath("/"); // 전체 도메인에서 접근 가능하도록 설정 -> 나중에 특정 도메인에서 접근 가능하도록 설정
            memberCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효 기간 (1일) -> 나중에 시간 1시간이나 짧은시간으로 변경하기
            response.addCookie(memberCookie);

            // 리디렉션 URL 설정
            String redirectUrl = REDIRECT_URL; // 추가정보 입력 페이지 URL로 나중에 설정하기.

            // 리디렉션
            response.sendRedirect(redirectUrl);
        } else {
            // 회원 정보가 없을때 예외
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "없는 회원 입니다.");
        }
    }
}
