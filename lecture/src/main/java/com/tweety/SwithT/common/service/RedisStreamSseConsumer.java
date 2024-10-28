package com.tweety.SwithT.common.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.stream.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RedisStreamSseConsumer implements StreamListener<String, MapRecord<String, String, String>> {

    @Autowired
    @Qualifier("4")
    private RedisTemplate<String, Object> redisTemplate;

    private final ConcurrentHashMap<String, SseEmitter> clients = new ConcurrentHashMap<>();

    @Override
    public void onMessage(MapRecord<String, String, String> record) {
        String recordId = record.getId().getValue(); // Redis Stream 고유 ID
        String memberId = record.getValue().get("memberId");
        String messageType = record.getValue().get("messageType");
        String title = record.getValue().get("title");
        String contents = record.getValue().get("contents");

        SseEmitter emitter = clients.get(memberId);

        if(emitter != null){
            try {
                Map<String, String> structuredMessage = new HashMap<>();
                structuredMessage.put("id", recordId); // Redis Stream 고유 ID
                structuredMessage.put("messageType", messageType);
                structuredMessage.put("title", title);
                structuredMessage.put("contents", contents);

                ObjectMapper objectMapper = new ObjectMapper();
                String jsonMessage = objectMapper.writeValueAsString(structuredMessage);


                    emitter.send(SseEmitter.event()
                            .name("notification")
                            .data(jsonMessage));



                String groupName = null;
                String streamName = null;
                if(messageType.equals("결제요청")){
                    streamName = "sse-notifications";
                    groupName = "sse-group";
                }else if(messageType.equals("WAITING")){
                    streamName = "waiting-notifications";
                    groupName = "waiting-group";
                }
                if(groupName != null){
                    redisTemplate.opsForStream().acknowledge(streamName, groupName, record.getId());
                }else {
                    System.out.println("groupName 이 존재하지 않음");
                }


            } catch (IOException e) {
                System.out.println("IOException "+ e.getMessage());

                emitter.completeWithError(e);
                clients.remove(memberId);
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                        Consumer.from("sse-group", memberId),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create("sse-notifications", ReadOffset.lastConsumed())
                );
            }catch (IllegalStateException e2) {
                System.out.println("illegalStateException "+ e2.getMessage());
                clients.remove(memberId);
                List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                        Consumer.from("sse-group", memberId),
                        StreamReadOptions.empty().count(1),
                        StreamOffset.create("sse-notifications", ReadOffset.lastConsumed())
                );
            }
        } else {
            System.out.println(memberId+" emitter 없음. pending 처리될 record id : "+record.getId().getValue());
            List<MapRecord<String, Object, Object>> records = redisTemplate.opsForStream().read(
                    Consumer.from("sse-group", memberId),
                    StreamReadOptions.empty().count(1),
                    StreamOffset.create("sse-notifications", ReadOffset.lastConsumed())
            );

            for(MapRecord<String, Object, Object> rs: records){
                System.out.println(rs);
            }
            System.out.println("pending 처리됨");
        }
    }

    public void addClient(String memberId, SseEmitter emitter) {
        clients.put(memberId, emitter);
        //로그인 후 pending 메세지 처리
        processPendingMessage(memberId, emitter);
    }

    public void removeClient(String memberId) {
        clients.remove(memberId);
    }

    //로그인 전 pending 처리된 메세지 가져오기
    public void processPendingMessage(String memberId, SseEmitter emitter){
        System.out.println("processPendingMessage : pending 메세지 처리");

        var records = redisTemplate.opsForStream().read(
                Consumer.from("sse-group", memberId),
                StreamReadOptions.empty(),
                StreamOffset.create("sse-notifications", ReadOffset.from("0"))
        );

        if (records != null && !records.isEmpty()) {
            records.forEach(record -> {
                try {
                    System.out.println(record+"이거 pending 해결됨");
                    // 메시지를 다시 처리
                    Map<String, String> structuredMessage = new HashMap<>();
                    structuredMessage.put("id", record.getId().getValue());  // Redis Stream ID
                    Map<Object, Object> kvMap = record.getValue();
                    structuredMessage.put("messageType", kvMap.get("messageType").toString());
                    structuredMessage.put("title", kvMap.get("title").toString());
                    structuredMessage.put("contents", kvMap.get("contents").toString());

                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonMessage = objectMapper.writeValueAsString(structuredMessage);
                    try{
                        emitter.send(SseEmitter.event()
                                .name("notification")
                                .data(jsonMessage));
                        System.out.println("SSE notification: " + jsonMessage);

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                    // 메시지 처리 후 ACK 수행
                    redisTemplate.opsForStream().acknowledge("sse-notifications", "sse-group", record.getId());
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                }
            });
        } else {
            System.out.println("pending 메세지 없음: " + memberId);
        }
    }


//    private MapRecord<String, String, String> convertRecord(MapRecord<String, Object, Object> record) {
//        Map<String, String> valueMap = new HashMap<>();
//        record.getValue().forEach((key, value) -> valueMap.put(String.valueOf(key), String.valueOf(value)));
//
//        return StreamRecords.newRecord()
//                .in(record.getStream())
//                .withId(record.getId())
//                .ofMap(valueMap);
//    }


    // 메시지 ACK 처리 메서드
//    private void acknowledgeMessage(MapRecord<String, String, String> record) {
//        String streamName = record.getStream();
//        String groupName = "sse-group";  // 그룹 이름 고정 (필요 시 동적 설정 가능)
//
//        redisTemplate.opsForStream().acknowledge(streamName, groupName, record.getId());
//        System.out.println("ACK message id : " + record.getId().getValue());
//    }
}
