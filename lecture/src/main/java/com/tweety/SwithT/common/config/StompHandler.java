package com.tweety.SwithT.common.config;

import ch.qos.logback.core.spi.ErrorCodes;
import com.tweety.SwithT.common.auth.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {
    private final JwtTokenProvider jwtTokenProvider;

    public StompHandler(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        System.out.println("StompHandler에서 처리");

        if(accessor.getCommand() == StompCommand.CONNECT) {
            System.out.println("연결 요청");
        }
        if(accessor.getCommand() == StompCommand.SUBSCRIBE) {
            System.out.println("구독 요청");
        }
        if(accessor.getCommand() == StompCommand.SEND){
            System.out.println("전송 요청");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);  // 'Bearer ' 부분 제거
                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰에서 인증 정보를 가져와 SecurityContext에 설정
                    System.out.println("SecurityContext 생성");
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }
            }

        }

        if(accessor.getCommand() == StompCommand.DISCONNECT){
            System.out.println("disconnect 요청");
            String bearerToken = accessor.getFirstNativeHeader("Authorization");

            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                String token = bearerToken.substring(7);  // 'Bearer ' 부분 제거
                if (jwtTokenProvider.validateToken(token)) {
                    // 토큰에서 인증 정보를 가져와 SecurityContext에 설정
                    System.out.println("SecurityContext 생성");
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
                }
            }

        }
        return message;


    }




}