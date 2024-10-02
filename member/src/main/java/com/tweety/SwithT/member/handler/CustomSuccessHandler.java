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

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        Optional<Member> member = memberRepository.findByEmail(email); // 회원 ID 조회
//
        System.out.println("핸들러!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println(member.get().getId());
        System.out.println(member.get().getName());
        System.out.println(member.get().getEmail());
        System.out.println(member.get().getRole());


//        // 토큰 생성
//        String jwtToken = jwtTokenProvider.createToken(String.valueOf(member.get().getId()), email, "ROLE_USER", name);
//        String refreshToken = jwtTokenProvider.createRefreshToken(String.valueOf(memberId), email, "ROLE_USER", name);

//        // Redis에 Refresh Token 저장
//        redisTemplate.opsForValue().set(email, refreshToken, 240, TimeUnit.HOURS);

//        // 응답 정보 설정
//        Map<String, Object> loginInfo = new HashMap<>();
//        loginInfo.put("id", memberId);
//        loginInfo.put("token", jwtToken);
//        loginInfo.put("refreshToken", refreshToken);
//        loginInfo.put("redirectUri", "http://localhost:8082/additionalInfo"); // 추가정보 입력 URI
//
//        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Login is successful", loginInfo);
//
//        // JSON 형태로 응답 반환
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(new ObjectMapper().writeValueAsString(commonResDto));
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
