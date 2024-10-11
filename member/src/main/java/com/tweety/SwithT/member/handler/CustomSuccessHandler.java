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
    private final String REDIRECT_URL = "http://localhost:8081/member/explain"; //테스트를 위해서 임시로 뒀음.
    private final String REDIRECT_URL_EXIST = "http://localhost:8081/"; //테스트를 위해서 임시로 뒀음.

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
            String name = existingMember.getName();
            Long memberId = existingMember.getId();


            Cookie memberCookie = new Cookie("memberId", String.valueOf(memberId));
            memberCookie.setPath("/"); // 전체 도메인에서 접근 가능하도록 설정 -> 추후 추가정보 입력화면만 하도록 수정 예정
            memberCookie.setMaxAge(60 * 60 * 24); // 쿠키 유효 기간 (1일) -> 추후 1시간 혹은 10분으로 수정 예정
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
