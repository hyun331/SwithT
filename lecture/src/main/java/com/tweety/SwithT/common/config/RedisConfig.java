package com.tweety.SwithT.common.config;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import net.javacrumbs.shedlock.provider.redis.spring.RedisLockProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;

@Configuration
public class RedisConfig {

    @Value("${spring.redis.host}")
    public String host;

    @Value("${spring.redis.port}")
    public int port;

//    // ** 임시 추가
//    @Bean
//    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
//        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
//        redisTemplate.setConnectionFactory(redisConnectionFactory);
//        return redisTemplate;
//    }

    //결제 요청
    @Bean
    @Qualifier("4")
    public RedisConnectionFactory streamConnectionFactory() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setHostName(host);
        configuration.setPort(port);
        configuration.setDatabase(3); // Use database 3 for Redis Streams
        return new LettuceConnectionFactory(configuration);
    }

    @Bean
    @Qualifier("4")
    public RedisTemplate<String, Object> redisStreamTemplate(@Qualifier("4") RedisConnectionFactory streamConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(streamConnectionFactory);

        redisTemplate.setKeySerializer(new StringRedisSerializer());

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

    @Bean
    public RedisLockProvider lockProvider(RedisConnectionFactory redisConnectionFactory) {
        return new RedisLockProvider(redisConnectionFactory);
    }


    // redis 사용해서 스트림 기반 메세지를 저장 처리
    @Bean
    @Qualifier("4")
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamMessageListenerContainer(@Qualifier("4") RedisConnectionFactory streamConnectionFactory) {
        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .batchSize(10)
                        .build();

        return StreamMessageListenerContainer.create(streamConnectionFactory, options);
    }


    @Bean
    @Qualifier("5")
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(4);
        return new LettuceConnectionFactory(config);
    }

    @Bean
    @Qualifier("5")
    public RedisTemplate<String, Object> redisTemplate(@Qualifier("5") RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // Set key serializer to StringRedisSerializer for human-readable keys
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // Create and configure ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Support for Java 8 date/time types
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Use ISO-8601 format for dates

        // Use ObjectMapper in Jackson2JsonRedisSerializer directly
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Set value and hash value serializers to JSON
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

    @Bean
    @Qualifier("15")
    public RedisConnectionFactory redisCachingConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(14); // DB 14 사용
        return new LettuceConnectionFactory(config);
    }

//    조회수 캐싱용 redis
    @Bean
    @Qualifier("15")
    public RedisTemplate<String, Object> redisCachingTemplate(@Qualifier("15") RedisConnectionFactory redisCachingConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisCachingConnectionFactory);

        // Key Serializer 설정 (StringRedisSerializer 사용)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // ObjectMapper 생성 및 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 날짜 형식 사용

        // GenericJackson2JsonRedisSerializer 설정
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Value 및 Hash Value Serializer 설정
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }

    @Bean
    @Qualifier("14")
    public RedisConnectionFactory redisOpenSearchSyncConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setDatabase(13); // DB 13 사용
        return new LettuceConnectionFactory(config);
    }

    // openSearch Sync용 redis
    @Bean
    @Qualifier("14")
    public RedisTemplate<String, String> redisOpenSearchSyncTemplate(@Qualifier("15") RedisConnectionFactory redisOpenSearchSyncConnectionFactory) {
        RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisOpenSearchSyncConnectionFactory);

        // Key Serializer 설정 (StringRedisSerializer 사용)
        redisTemplate.setKeySerializer(new StringRedisSerializer());

        // ObjectMapper 생성 및 설정
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Java 8 날짜/시간 지원
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 날짜 형식 사용

        // GenericJackson2JsonRedisSerializer 설정
        GenericJackson2JsonRedisSerializer serializer = new GenericJackson2JsonRedisSerializer(objectMapper);

        // Value 및 Hash Value Serializer 설정
        redisTemplate.setValueSerializer(serializer);
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }
}

