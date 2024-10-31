package com.tweety.SwithT.common.configs;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Configuration
public class KafkaTopicConfig {

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

        try (AdminClient adminClient = AdminClient.create(config)) {
            if (!adminClient.listTopics().names().get().contains(topicName)) {
                NewTopic topic = new NewTopic(topicName, numPartitions, replicationFactor);
                adminClient.createTopics(List.of(topic));
                System.out.println("Kafka 토픽 생성됨: " + topicName);
            } else {
                System.out.println("Kafka 토픽이 이미 존재합니다: " + topicName);
            }
        } catch (InterruptedException | ExecutionException e) {
            System.err.println("토픽 확인 또는 생성 중 오류 발생: " + e.getMessage());
            Thread.currentThread().interrupt();
        }
    }
}
