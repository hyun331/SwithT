package com.tweety.SwithT.common.config;

import com.tweety.SwithT.common.service.RedisStreamSseConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer.StreamMessageListenerContainerOptions;
import org.springframework.stereotype.Component;


//redis stream 지속 수신
@Component
public class RedisStreamListenerConfig {

    @Autowired
    private RedisStreamSseConsumer redisStreamSseConsumer;

    @Autowired
    @Qualifier("4")
    private RedisTemplate<String, Object> redisTemplate;

    private static final String STREAM_NAME = "sse-notifications";
    private static final String WAITING_STREAM_NAME = "waiting-notifications";
    private static final String SSE_GROUP = "sse-group";
    private static final String WAITING_GROUP = "waiting-group";

    @EventListener(ContextRefreshedEvent.class)
    public void start() {
        createGroup(STREAM_NAME, SSE_GROUP);
        createGroup(WAITING_STREAM_NAME, WAITING_GROUP);

        StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)  // 배치크기설정
                        .build();

        StreamMessageListenerContainer<String, MapRecord<String, String, String>> container =
                StreamMessageListenerContainer.create(redisTemplate.getConnectionFactory(), options);

        //리스너 컨테이너를 redis stream에 연결
        container.receive(StreamOffset.create(STREAM_NAME, ReadOffset.lastConsumed()), redisStreamSseConsumer);

        container.receive(StreamOffset.create(WAITING_STREAM_NAME, ReadOffset.lastConsumed()), redisStreamSseConsumer);

        container.start();
    }

    //각 stream당 consumer group 생성
    private void createGroup(String streamName, String groupName) {
        try {
            redisTemplate.opsForStream().createGroup(streamName, ReadOffset.latest(), groupName);
        } catch (Exception e) {
            System.out.println("Group already exists: " + e.getMessage());
        }
    }
}
