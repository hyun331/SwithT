package com.tweety.SwithT.common.controller;

import com.tweety.SwithT.common.service.RedisStreamSseConsumer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@RestController
public class SseController {

    private final StreamMessageListenerContainer<String, ?> streamMessageListenerContainer;
    private final RedisStreamSseConsumer redisStreamSseConsumer;


    public SseController(@Qualifier("4")  StreamMessageListenerContainer<String, ?> streamMessageListenerContainer, RedisStreamSseConsumer redisStreamSseConsumer) {
        this.streamMessageListenerContainer = streamMessageListenerContainer;
        this.redisStreamSseConsumer = redisStreamSseConsumer;
    }

    //연결이 들어올 수 있도록 api 생성
    @GetMapping("/subscribe")
    public SseEmitter subscribe(){
        SseEmitter emitter = new SseEmitter(14400*60*1000L); // 정도로 emitter유효시간 설정
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        redisStreamSseConsumer.addClient(memberId, emitter);

        emitter.onCompletion(()->redisStreamSseConsumer.removeClient(memberId));   //할거 다하면 emitters에서 제거
        emitter.onTimeout(()->redisStreamSseConsumer.removeClient(memberId));      //시간 지나면 emitters에서 제거

        try{
            emitter.send(SseEmitter.event().name("connect").data("connected!!!!"));
        }catch(IOException e){
            emitter.completeWithError(e);
            e.printStackTrace();
        }
        return emitter;
    }
}
