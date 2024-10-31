package com.tweety.SwithT.common.configs;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Configuration
public class KafkaTopicConfig {

    private static final Logger logger = LoggerFactory.getLogger(KafkaTopicConfig.class);

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(configs);
    }

    public void createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor) {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        int retries = 3;  // 재시도 횟수
        for (int attempt = 1; attempt <= retries; attempt++) {
            try (AdminClient adminClient = AdminClient.create(config)) {
                // 토픽이 존재하지 않을 경우에만 생성
                if (!adminClient.listTopics().names().get().contains(topicName)) {
                    NewTopic topic = new NewTopic(topicName, numPartitions, replicationFactor);
                    adminClient.createTopics(List.of(topic)).all().get(10, TimeUnit.SECONDS);
                    logger.info("Kafka 토픽이 생성되었습니다: {}", topicName);
                    break;
                } else {
                    logger.info("Kafka 토픽이 이미 존재합니다: {}", topicName);
                    break;
                }
            } catch (ExecutionException e) {
                logger.error("토픽 생성 중 ExecutionException 발생: {}", e.getMessage());
            } catch (InterruptedException e) {
                logger.error("토픽 생성 중 InterruptedException 발생: {}", e.getMessage());
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                logger.error("토픽 생성 시 알 수 없는 오류 발생: {}", e.getMessage());
            }

            // 재시도 대기
            try {
                TimeUnit.SECONDS.sleep(2);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            logger.warn("Kafka 토픽 생성 재시도 중 (시도 횟수: {}/{})", attempt, retries);
        }
    }
}
