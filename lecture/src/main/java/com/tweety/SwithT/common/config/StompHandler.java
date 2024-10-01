package com.tweety.SwithT.common.config;

import ch.qos.logback.core.spi.ErrorCodes;
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
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class StompHandler implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
//        StompCommand commandType = accessor.getCommand();
//
//        String bearerToken = accessor.getFirstNativeHeader("Authorization").substring(7);
//
//        if(StompCommand.CONNECT == commandType){
//            //웹소켓 연결 요청 시
//            if(bearerToken == null || !bearerToken.startsWith("Bearer ")){
//                throw new EntityNotFoundException("채팅 불가. 로그인 먼저");
//            }
//        }
//        else if(StompCommand.SEND == commandType){
//            //pub
//            String destination = accessor.getDestination();
//            log.info("Destination: " + destination);
//
//        }
//        else if(StompCommand.SUBSCRIBE == commandType){
//            //sub
//            log.info("sub");
//
//        }
//        String bearerToken = accessor.getFirstNativeHeader("Authorization");
//        if (bearerToken == null) {
//            throw new EntityNotFoundException("로그인 이후에 채팅이 가능합니다.");
//        }
//        log.info("stomp handler. token : "+bearerToken.substring(7));

//        String token = bearerToken.substring(7);
//
//        if(accessor.getCommand() == StompCommand.CONNECT){
//        }
//        else if(accessor.getCommand() == StompCommand.SUBSCRIBE){
//
//        }
//        else if(accessor.getCommand() == StompCommand.SEND){
//
//        }
//        else if(accessor.getCommand() == StompCommand.DISCONNECT){
//
//        }
        return message;


    }




}