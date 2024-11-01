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

@Component
public class CustomSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    //배포 용도 주소
    private final String REDIRECT_URL = "https://www.switht.co.kr/member/explain";
    private final String REDIRECT_URL_EXIST = "https://www.switht.co.kr/loginSuccess";//테스트를 위해서 임시로 뒀음.
    // 로컬 용도 주소
//    private final String REDIRECT_URL = "http://localhost:8081/member/explain";
//    private final String REDIRECT_URL_EXIST = "http://localhost:8081/loginSuccess";

    public CustomSuccessHandler(JwtTokenProvider jwtTokenProvider, MemberRepository memberRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.memberRepository = memberRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");


        Optional<Member> member = memberRepository.findByEmail(email);

        if (member.isPresent()) {
            Member existingMember = member.get();
            Long memberId = existingMember.getId();

            // JWT 토큰 생성
            String accessToken =
                    jwtTokenProvider.createToken(String.valueOf(memberId),email, String.valueOf(existingMember.getRole()),String.valueOf(existingMember.getName()));

            String refreshToken =
                    jwtTokenProvider.createRefreshToken(String.valueOf(memberId),email, String.valueOf(existingMember.getRole()),String.valueOf(existingMember.getName()));


//            배포용도 코드
//            // Access Token을 쿠키에 저장 (HttpOnly로 설정하여 보안 강화)
//            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//            accessTokenCookie.setDomain("switht.co.kr");
//            accessTokenCookie.setHttpOnly(false);  //
//            accessTokenCookie.setPath("/");
//            accessTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
//            response.addCookie(accessTokenCookie);
//
//            // Refresh Token을 쿠키에 저장
//            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//            refreshTokenCookie.setDomain("switht.co.kr");
//            refreshTokenCookie.setHttpOnly(false); // 이게 true로 설정되어있으면 쿠키값 못 가져옴.
//            refreshTokenCookie.setPath("/");
//            refreshTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
//            response.addCookie(refreshTokenCookie);
//
//            // memberId도 쿠키에 저장
//            Cookie memberCookie = new Cookie("memberId", String.valueOf(memberId));
//            memberCookie.setDomain("switht.co.kr");
//            memberCookie.setHttpOnly(false);
//            memberCookie.setSecure(true);
//            memberCookie.setPath("/");
//            memberCookie.setMaxAge(60 * 60 );  // 1시간 유지
//            response.addCookie(memberCookie);

            //            // Access Token을 쿠키에 저장 (HttpOnly로 설정하여 보안 강화)
//            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
//            accessTokenCookie.setDomain("switht.co.kr");
//            accessTokenCookie.setHttpOnly(false);  //
//            accessTokenCookie.setPath("/");
//            accessTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
//            response.addCookie(accessTokenCookie);
//
//            // Refresh Token을 쿠키에 저장
//            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
//            refreshTokenCookie.setDomain("switht.co.kr");
//            refreshTokenCookie.setHttpOnly(false); // 이게 true로 설정되어있으면 쿠키값 못 가져옴.
//            refreshTokenCookie.setPath("/");
//            refreshTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
//            response.addCookie(refreshTokenCookie);
//
//            // memberId도 쿠키에 저장
//            Cookie memberCookie = new Cookie("memberId", String.valueOf(memberId));
//            memberCookie.setDomain("switht.co.kr");
//            memberCookie.setHttpOnly(false);
//            memberCookie.setSecure(true);
//            memberCookie.setPath("/");
//            memberCookie.setMaxAge(60 * 60 );  // 1시간 유지
//            response.addCookie(memberCookie);



//            🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟🌟
//          배포용도 코드
//          Access Token을 쿠키에 저장 (HttpOnly로 설정하여 보안 강화)
            Cookie accessTokenCookie = new Cookie("accessToken", accessToken);
            accessTokenCookie.setHttpOnly(false);  //
            accessTokenCookie.setPath("/");
            accessTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
            response.addCookie(accessTokenCookie);

            // Refresh Token을 쿠키에 저장
            Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
            refreshTokenCookie.setHttpOnly(false); // 이게 true로 설정되어있으면 쿠키값 못 가져옴.
            refreshTokenCookie.setPath("/");
            refreshTokenCookie.setMaxAge(60 * 60);  // 1시간 유지
            response.addCookie(refreshTokenCookie);

            // memberId도 쿠키에 저장
            Cookie memberCookie = new Cookie("memberId", String.valueOf(memberId));
            memberCookie.setHttpOnly(false);
            memberCookie.setPath("/");
            memberCookie.setMaxAge(60 * 60 );  // 1시간 유지
            response.addCookie(memberCookie);

            // phoneNumber 필드가 null인지 확인
            if (existingMember.getPhoneNumber() == null) {
                // 첫 번째 로그인 -> 추가 정보 입력 페이지로 리디렉션
                String redirectUrl = REDIRECT_URL; // 추가정보 입력 페이지 URL
                response.sendRedirect(redirectUrl);
            } else {
                // 두 번째 이후 로그인 -> 메인 페이지로 리디렉션
                String redirectUrl = REDIRECT_URL_EXIST; // 메인 페이지 URL
                response.sendRedirect(redirectUrl);
            }

        } else {
            // 회원 정보가 없을때 예외
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "없는 회원 입니다. 관리자에게 문의하세요.");
        }
    }

}
