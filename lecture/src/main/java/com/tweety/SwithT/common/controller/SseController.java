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
    //SseEmitter : 연결된 사용자 정보를 의미
    //ConcurrentHashMap : Thread-safe한 map = 멀티 스레드 상황에서 안전 => 동시성 이슈 발생 안함

    //여러번 구독을 방지하기 위한 ConcurrentHashSet 변수 생성
//    private Set<String> subscribeList = ConcurrentHashMap.newKeySet();
    private final StreamMessageListenerContainer<String, ?> streamMessageListenerContainer;
    private final RedisStreamSseConsumer redisStreamSseConsumer;


    public SseController(@Qualifier("4")  StreamMessageListenerContainer<String, ?> streamMessageListenerContainer, RedisStreamSseConsumer redisStreamSseConsumer) {
        this.streamMessageListenerContainer = streamMessageListenerContainer;
        this.redisStreamSseConsumer = redisStreamSseConsumer;
    }
    private static final String STREAM_NAME = "sse-notifications";



    //연결이 들어올 수 있도록 api 생성
    @GetMapping("/subscribe")
    public SseEmitter subscribe(){
        SseEmitter emitter = new SseEmitter(14400*60*1000L); // 정도로 emitter유효시간 설정
        String memberId = SecurityContextHolder.getContext().getAuthentication().getName();

        redisStreamSseConsumer.addClient(memberId, emitter);

        emitter.onCompletion(()->redisStreamSseConsumer.removeClient(memberId));   //할거 다하면 emitters에서 제거
        emitter.onTimeout(()->redisStreamSseConsumer.removeClient(memberId));      //시간 지나면 emitters에서 제거

        try{
//            연결을 요청한 emitter에게 connect라는 event 연결되었다고 보내기
            emitter.send(SseEmitter.event().name("connect").data("connected!!!!"));
        }catch(IOException e){
            emitter.completeWithError(e);
            e.printStackTrace();
        }
        return emitter;
    }


}
