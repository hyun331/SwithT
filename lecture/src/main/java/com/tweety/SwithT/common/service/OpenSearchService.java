package com.tweety.SwithT.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

@Service
public class OpenSearchService {

    private final HttpClient client = HttpClient.newHttpClient(); // HTTP 클라이언트 초기화
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 초기화

    @Value("${opensearch.url}")
    private String openSearchUrl; // OpenSearch URL

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey; // AWS 접근 키

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey; // AWS 비밀 키

    @Value("${opensearch.region}")
    private String region; // AWS 리전

    // OpenSearch 클라이언트 생성 메서드
    public OpenSearchClient createOpenSearchClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return OpenSearchClient.builder()
                .region(Region.of(region)) // 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds)) // 자격 증명 설정
                .endpointOverride(URI.create(openSearchUrl)) // OpenSearch 엔드포인트 설정
                .build();
    }

    // OpenSearch에 강의 등록
    public void registerLecture(LectureDetailResDto lecture) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lectures/_doc/" + lecture.getId(); // 강의 ID를 포함한 요청 엔드포인트
        String requestBody = objectMapper.writeValueAsString(lecture); // LectureDetailResDto를 JSON으로 변환

        // PUT 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json") // 요청 헤더 설정
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody)) // 요청 본문 설정
                .build();

        // 요청 전송 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // 성공 응답이 아닐 경우 예외 발생
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IOException("OpenSearch에 강의 등록 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의 삭제
    public void deleteLecture(Long lectureId) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lectures/_doc/" + lectureId; // 삭제할 강의 ID를 포함한 요청 엔드포인트

        // DELETE 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .DELETE() // DELETE 방식 설정
                .build();

        // 요청 전송 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // 성공 응답이 아닐 경우 예외 발생
        if (response.statusCode() != 200) {
            throw new IOException("OpenSearch에서 강의 삭제 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의를 검색하는 메서드
    public List<LectureDetailResDto> searchLectures(String keyword, Pageable pageable) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lectures/_search"; // 검색 요청 엔드포인트
        // 검색 쿼리 생성
        String requestBody = String.format("""
            {
                "query": {
                    "multi_match": {
                        "query": "%s",
                        "fields": ["title", "contents", "memberName"]
                    }
                },
                "from": %d,
                "size": %d
            }
            """, keyword, pageable.getOffset(), pageable.getPageSize()); // 페이지 정보 추가

        // POST 요청 생성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json") // 요청 헤더 설정
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // 요청 본문 설정
                .build();

        // 요청 전송 및 응답 받기
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // 성공 응답일 경우 검색 결과 반환
        if (response.statusCode() == 200) {
            return parseSearchResults(response.body()); // 응답 결과 파싱하여 반환
        } else {
            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
        }
    }

    // OpenSearch 응답을 LectureDetailResDto 리스트로 변환하는 메서드
    private List<LectureDetailResDto> parseSearchResults(String responseBody) throws IOException {
        List<LectureDetailResDto> lectureList = new ArrayList<>(); // 결과 리스트 초기화
        JsonNode jsonNode = objectMapper.readTree(responseBody); // JSON 응답 파싱
        JsonNode hits = jsonNode.path("hits").path("hits"); // hits 노드 접근

        // 각 검색 결과를 LectureDetailResDto로 변환
        for (JsonNode hit : hits) {
            JsonNode source = hit.path("_source"); // _source 노드 접근
            LectureDetailResDto lecture = objectMapper.treeToValue(source, LectureDetailResDto.class); // DTO 변환
            lectureList.add(lecture); // 리스트에 추가
        }

        return lectureList; // 변환된 리스트 반환
    }
}
