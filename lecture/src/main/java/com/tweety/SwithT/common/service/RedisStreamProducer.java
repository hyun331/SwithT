package com.tweety.SwithT.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamReceiver;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RedisStreamProducer {

    @Autowired
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
