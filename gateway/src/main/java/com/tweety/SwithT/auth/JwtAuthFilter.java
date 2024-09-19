
package com.tweety.SwithT.auth;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class JwtAuthFilter extends GenericFilter {

    @Value("${jwt.secretKey}")
    private String secretKey;

    private final HttpServletResponse httpServletResponse;


    public JwtAuthFilter(HttpServletResponse httpServletResponse) {
        this.httpServletResponse = httpServletResponse;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String bearerToken = ((HttpServletRequest) servletRequest).getHeader("Authorization");

        try {

            if( bearerToken != null){

                //token은 관례적으로 Bearer 로 시작하는 문구를 넣어서 요청
                if(!bearerToken.substring(0,7).equals("Bearer ")){ //관례적으로 붙인다고 보면 된다 "Bearer "
                    //스트링의 0번째부터 7번쨰까지 "Bearer "이 문자열이 아니면 베어러토큰이 아니기 때문에 팅긴다.
                    throw new AuthenticationServiceException("Bearer 형식이 아닙니다.");
                }

                String token = bearerToken.substring(7);

//            token 검증 및 claims(사용자 정보) 추출
//            token 생성 시에 사용한 secret key 값을 넣어 토큰 검증에 사용
                // claims은 사실상 페이로드다. 검증은 서명부로 하나 서버가 필요한 정보는 페이로드에서 꺼낸다.
                // jwts를 파싱해서 setSigningKey(우리 시크릿키를)를 넣어서 다시 암호화 해보는 것 이다. 그렇게 검증 하는 것
                Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody(); //이 한줄이 검증을 위한 한줄이다
//            Authentication 객체 생성(userDetails 객체도 필요)
                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority("ROLE_" + claims.get("role")));
                UserDetails userDetails = new User(claims.getSubject(), "", authorities/*권한자리*/);
//            UserDetails 인터페이스를 상속한 User 객체를 통해 검증 후에 Authentication 생성
                //전역적으로 가져다가 사용하려고 Authentication 객체를 사용하려고 만드는 것 이다.
                Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);

            }
//        filterchain 에서 그 다음 filtering으로 넘어가도록 하는 메서드
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e){
            log.error(e.getMessage());
            HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
            httpServletResponse.setStatus(HttpStatus.UNAUTHORIZED.value());
            httpServletResponse.setContentType("application/json");
            httpServletResponse.getWriter().write("token error");
        }
    }
}
