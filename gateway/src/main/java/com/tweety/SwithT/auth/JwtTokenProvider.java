package com.tweety.SwithT.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private int expiration;

    @Value("${jwt.secretKey}")
    private String secretKeyRt;

    @Value("${jwt.expirationRt}") //오타 조심
    private int expirationRt;



    // 여기서 토큰을 만들어서 사용자에게 주는 것 이다.
    public String createToken(String email,String role){

        //헤더,페이로드,시그니처 를 기억해라

        // Claims는 사용자 정보이자 페이로드 정보이다.
        Claims claims = Jwts.claims().setSubject(email); // 페이로드부에 email을 셋팅해준다
        claims.put("role", role); // 그 다음 Role을 셋팅해준다
        Date now = new Date(); // 현재시간도 셋팅해주고
        String token = Jwts.builder()
                .setClaims(claims)            //시간 셋팅
                .setExpiration(new Date(now.getTime() + expiration * 60 * 1000L)) //만료시간
                .setIssuedAt(now) //생성시간
                //토큰을 만들려면 서명값을 넣어줘야한다.
                .signWith(SignatureAlgorithm.HS256, secretKey)
                //여기만 바꾸면 안돼 필터코드도 바꿔야해
                .compact();

        return token;

    }

    //약간만 달라진다
    public String createRefreshToken(String email,String role){

        //헤더,페이로드,시그니처 를 기억해라

        // Claims는 사용자 정보이자 페이로드 정보이다.
        Claims claims = Jwts.claims().setSubject(email); // 페이로드부에 email을 셋팅해준다
        claims.put("role", role); // 그 다음 Role을 셋팅해준다
        Date now = new Date(); // 현재시간도 셋팅해주고
        String token = Jwts.builder()
                .setClaims(claims)            //시간 셋팅
                .setExpiration(new Date(now.getTime() + expirationRt * 60 * 1000L)) //만료시간
                .setIssuedAt(now) //생성시간
                //토큰을 만들려면 서명값을 넣어줘야한다.
                .signWith(SignatureAlgorithm.HS256, secretKeyRt)
                //여기만 바꾸면 안돼 필터코드도 바꿔야해
                .compact();

        return token;

    }
}
