//package com.tweety.SwithT.member.handler;
//
//import com.tweety.SwithT.common.auth.JwtTokenProvider;
//import com.tweety.SwithT.member.domain.Member;
//import com.tweety.SwithT.member.repository.MemberRepository;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
//import org.springframework.stereotype.Component;
//import org.springframework.web.util.UriComponentsBuilder;
//
//import java.io.IOException;
//import java.util.Map;
//
//@Slf4j
//@RequiredArgsConstructor
//@Component
//public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final MemberRepository memberRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
//        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
//        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
//        String email = null;
//
//        // 소셜 로그인 제공자에 따른 이메일 추출
//        if ("google".equals(registrationId)) {
//            email = oAuth2User.getAttribute("email");
//        } else if ("kakao".equals(registrationId)) {
//            Map<String, Object> kakaoAccount = oAuth2User.getAttribute("kakao_account");
//            if (kakaoAccount != null && kakaoAccount.containsKey("email")) {
//                email = (String) kakaoAccount.get("email");
//            }
//        } else if ("naver".equals(registrationId)) {
//            Object naverObject = oAuth2User.getAttributes().get("response");
//            Map<String, Object> naverAccount = (Map<String, Object>) naverObject;
//            if (naverAccount != null && naverAccount.containsKey("email")) {
//                email = (String) naverAccount.get("email");
//            }
//        }
//
//        if (email == null) {
//            throw new RuntimeException("이메일 정보를 가져올 수 없습니다.");
//        }
//
//        // DB에서 사용자 정보 조회
//        Member member = memberRepository.findByEmail(email)
//                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 소셜 회원입니다."));
//
//        // JWT 토큰 생성
//        String jwtToken = jwtTokenProvider.createToken(String.valueOf(member.getId()), member.getEmail(), member.getRole().toString(), member.getName());
//
//        // 리다이렉트 URL 설정 (클라이언트가 JWT를 받을 수 있는 엔드포인트)
//        String targetUrl = UriComponentsBuilder.fromUriString("https://www.teenkiri.site/loginSuccess")
//                .queryParam("token", jwtToken)
//                .build().toUriString();
//
//        // 리다이렉트 수행
//        getRedirectStrategy().sendRedirect(request, response, targetUrl);
//    }
//}