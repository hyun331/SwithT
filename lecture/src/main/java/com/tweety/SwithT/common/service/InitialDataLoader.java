package com.tweety.SwithT.common.service;

import com.tweety.SwithT.lecture_chat_room.repository.LectureChatRoomRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Component
public class InitialDataLoader implements CommandLineRunner {


    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Override
    public void run(String... args) {
        createTopicIfNotExists("chat-topic", 1, (short) 3);
        System.out.println("채팅방 미리 생성");
    }



    @Bean
    public KafkaAdmin kafkaAdmin() {
        Map<String, Object> configs = new HashMap<>();
        configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        configs.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "120000");  // 타임아웃 시간 증가
        return new KafkaAdmin(configs);
    }

    public void createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor) {
        Map<String, Object> config = new HashMap<>();
        config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(AdminClientConfig.REQUEST_TIMEOUT_MS_CONFIG, "120000"); // 타임아웃 시간 증가

        try (AdminClient adminClient = AdminClient.create(config)) {
            if (adminClient.listTopics().names().get().contains(topicName)) {
                System.out.println("Kafka 토픽이 이미 존재합니다: "+ topicName);
                return;
            }

            // 토픽이 존재하지 않으면 생성
            NewTopic topic = new NewTopic(topicName, numPartitions, replicationFactor);
            adminClient.createTopics(List.of(topic)).all().get(10, TimeUnit.SECONDS);
            System.out.println("Kafka 토픽이 생성되었습니다: "+ topicName);
        } catch (ExecutionException | InterruptedException e) {
            System.out.println("토픽 생성 중 오류 발생: "+ topicName+ e.getMessage());
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            System.out.println("토픽 생성 시 알 수 없는 오류 발생: "+ topicName+e.getMessage());
        }
    }

}
