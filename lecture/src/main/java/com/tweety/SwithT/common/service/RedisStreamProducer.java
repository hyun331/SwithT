package com.tweety.SwithT.common.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RedisStreamProducer {


    @Qualifier("4")
    private RedisTemplate<String, String> redisTemplate;

    private static final String STREAM_NAME = "sse-notifications";

    public RecordId publishMessage(String memberId, String messageType, String title, String contents){
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("memberId", memberId);
        messageMap.put("messageType", messageType);
        messageMap.put("title", title);
        messageMap.put("contents", contents);


        ObjectRecord<String, Map<String, String>> record = StreamRecords.newRecord()
                .in(STREAM_NAME)
                .ofObject(messageMap);
        return redisTemplate.opsForStream().add(record);
    }
}
