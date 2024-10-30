package com.tweety.SwithT.common.service;

import com.tweety.SwithT.lecture_chat_room.repository.LectureChatRoomRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Component
public class InitialDataLoader implements CommandLineRunner {

    @Autowired
    private LectureChatRoomRepository lectureChatRoomRepository;

    @Value("${kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Override
    public void run(String... args) {
        // Example: Create multiple topics during startup
//        createTopic("chat-topic", 1, (short) 1);
        // Add more topics as needed
         createTopic("chat-topic", 1, (short) 1);
        System.out.println("채팅방 미리 생성");
    }

    private void createTopic(String topicName, int partitions, short replicationFactor) {
        Properties props = new Properties();
        props.put("bootstrap.servers", bootstrapServers);

        try (AdminClient adminClient = AdminClient.create(props)) {
            NewTopic topic = new NewTopic(topicName, partitions, replicationFactor);

            adminClient.createTopics(Collections.singletonList(topic)).all().get();
            System.out.println("토픽 생성 topic: " + topicName);
        } catch (ExecutionException e) {
            if (e.getCause() instanceof org.apache.kafka.common.errors.TopicExistsException) {
                System.out.println("이미 존재하는 토픽 : " + topicName);
            } else {
                System.err.println("토픽 생성 실패: " + topicName);
                e.printStackTrace();
            }
        } catch (Exception e) {
            System.err.println("Unexpected error 토픽 생성 시 : " + topicName);
            e.printStackTrace();
        }
    }

}
