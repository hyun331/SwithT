package com.tweety.SwithT.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import java.net.URI;

@Configuration
public class OpenSearchConfig {

    @Value("${spring.opensearch.url}")
    private String openSearchUrl;

    @Value("${spring.opensearch.region}")
    private String region;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.opensearch.index-settings.shards}")
    public int numberOfShards;

    @Value("${spring.opensearch.index-settings.replicas}")
    public int numberOfReplicas;

    @Bean
    public OpenSearchClient openSearchClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);

        return OpenSearchClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create(openSearchUrl))  // OpenSearch URL 설정
                .build();
    }
}
