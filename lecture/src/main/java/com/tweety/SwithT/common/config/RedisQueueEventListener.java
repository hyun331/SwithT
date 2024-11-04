package com.tweety.SwithT.common.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.common.service.RedisStreamProducer;
import com.tweety.SwithT.common.service.RedisStreamSseConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

@Service
public class RedisQueueEventListener implements MessageListener {

    @Autowired
    private RedisStreamProducer redisStreamProducer;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisStreamSseConsumer sseConsumer; // SSE 알림 전송기

    private final ObjectMapper objectMapper = new ObjectMapper();

    // Pub/Sub 메시지 수신 시 실행되는 메서드
    @Override
    public void onMessage(Message message, byte[] pattern) {
        String body = new String(message.getBody());
        Map<String, String> event = parseMessage(body);

        String eventType = event.get("eventType");
        String lectureGroupId = event.get("lectureGroupId");
        String memberId = event.get("memberId");

        if ("ENTRY".equals(eventType)) {
            handleQueueEntryEvent(lectureGroupId, memberId, event.get("rank"));
        } else if ("EXIT".equals(eventType)) {
            handleQueueExitEvent(lectureGroupId, memberId);
        }
    }

    private void handleQueueEntryEvent(String lectureGroupId, String memberId, String rank) {
        // 상위 50명에게만 실시간 위치 업데이트 알림
        if (Integer.parseInt(rank) < 50) {
            sseConsumer.sendSseNotification(lectureGroupId, memberId, "WAITING", "",rank);
        }
    }

    private void handleQueueExitEvent(String lectureGroupId, String memberId) {
        // 최상위 유저 알림 및 대기열에서 제거
        sseConsumer.sendSseNotification(lectureGroupId, memberId, "WAITING-SUCCESS", "","0");
        String queueKey = "lecture-queue-" + lectureGroupId;
        redisTemplate.opsForZSet().remove(queueKey, memberId);
    }

    // 메시지를 Map으로 파싱하는 유틸리티 메서드
    private Map<String, String> parseMessage(String body) {
        try {
            return objectMapper.readValue(body, new TypeReference<Map<String, String>>() {});
        } catch (IOException e) {
            throw new RuntimeException("메시지 파싱 오류", e);
        }
    }
}
