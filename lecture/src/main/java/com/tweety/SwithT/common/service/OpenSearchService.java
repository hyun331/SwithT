package com.tweety.SwithT.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
import com.tweety.SwithT.lecture.dto.LectureSearchDto;
import com.tweety.SwithT.lecture.repository.LectureRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.opensearch.OpenSearchClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class OpenSearchService {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${spring.opensearch.url}")
    private String openSearchUrl;

    @Value("${cloud.aws.credentials.access-key}")
    private String accessKey;

    @Value("${cloud.aws.credentials.secret-key}")
    private String secretKey;

    @Value("${spring.opensearch.region}")
    private String region;

    @Value("${spring.opensearch.username}")
    private String username;

    @Value("${spring.opensearch.password}")
    private String password;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final LectureRepository lectureRepository;

    @Autowired
    public OpenSearchService(LectureRepository lectureRepository) {
        this.lectureRepository = lectureRepository;
    }

    public OpenSearchClient createOpenSearchClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return OpenSearchClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .endpointOverride(URI.create(openSearchUrl))
                .build();
    }

    @PostConstruct
    public void init() {
        try {
            ensureIndexExists("lecture-service");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void ensureIndexExists(String indexName) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/" + indexName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) {
            createIndex(indexName);
        }
    }

    // S3에서 인덱스 설정 파일을 다운로드하는 메서드
    private String downloadIndexConfigFromS3(String bucketName, String key) throws IOException {
        S3Client s3 = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .build();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        InputStream s3InputStream = s3.getObject(getObjectRequest);

        BufferedReader reader = new BufferedReader(new InputStreamReader(s3InputStream, StandardCharsets.UTF_8));
        StringBuilder content = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }

        return content.toString();
    }

    private void createIndex(String indexName) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/" + indexName;

        // S3에서 인덱스 설정 파일 다운로드
        String key = "path/to/lecture-index.json";
        String indexMapping = downloadIndexConfigFromS3(bucketName, key);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .PUT(HttpRequest.BodyPublishers.ofString(indexMapping))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("인덱스 생성 실패: " + response.body());
        }
    }

    public void registerLecture(LectureDetailResDto lecture) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_doc/" + lecture.getId();
        String requestBody = objectMapper.writeValueAsString(lecture);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IOException("OpenSearch에 강의 등록 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의 삭제
    public void deleteLecture(Long lectureId) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_doc/" + lectureId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("OpenSearch에서 강의 삭제 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의를 검색하는 메서드
    public List<LectureDetailResDto> searchLectures(String keyword, Pageable pageable, LectureSearchDto searchDto) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_search";

        // 필터 조건을 구성하기 전에 빈 값인지 확인하여 필터링 처리
        List<String> filters = new ArrayList<>();

        System.out.println("category: " + searchDto.getCategory());
        System.out.println("status: " + searchDto.getStatus());
        System.out.println("lectureType: " + searchDto.getLectureType());
        // category 필터가 비어있으면 모든 카테고리 검색, 그렇지 않으면 카테고리 필터 추가
        if (searchDto.getCategory() != null && !searchDto.getCategory().isEmpty()) {
            filters.add(String.format("{\"match\": {\"category\": \"%s\"}}", searchDto.getCategory()));
        }

        // status 필터
        if (searchDto.getStatus() != null && !searchDto.getStatus().isEmpty()) {
            filters.add(String.format("{\"match\": {\"status\": \"%s\"}}", searchDto.getStatus()));
        }

        // lectureType 필터
        if (searchDto.getLectureType() != null && !searchDto.getLectureType().isEmpty()) {
            filters.add(String.format("{\"match\": {\"lectureType\": \"%s\"}}", searchDto.getLectureType()));
        }

        // 필터가 없으면 빈 배열로 처리, 있으면 join으로 연결
        String filterQuery = filters.isEmpty() ? "" : String.join(",", filters);

        // 필터가 있을 경우만 'filter'를 추가
        String requestBody;
        requestBody = String.format("""
    {
        "query": {
            "bool": {
                "must": [
                    {
                        "multi_match": {
                            "query": "%s",
                            "fields": ["title", "contents", "memberName"],
                            "type": "best_fields",
                            "fuzziness": "AUTO"
                        }
                    }
                ]%s
            }
        },
        "from": %d,
        "size": %d
    }
    """, keyword, filters.isEmpty() ? "" : String.format(", \"filter\": [%s]", filterQuery), pageable.getOffset(), pageable.getPageSize());

        // 쿼리 내용 로그 출력 (디버깅용)
        System.out.println("OpenSearch Request Body: " + requestBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println("response: " + response.body());
        if (response.statusCode() == 200) {
            return parseSearchResults(response.body());
        } else {
            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
        }
    }

    // OpenSearch 응답을 LectureDetailResDto 리스트로 변환하는 메서드
    private List<LectureDetailResDto> parseSearchResults(String responseBody) throws IOException {
        List<LectureDetailResDto> lectureList = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        JsonNode hits = jsonNode.path("hits").path("hits");

        for (JsonNode hit : hits) {
            JsonNode source = hit.path("_source");
            System.out.println("source" + source);
            LectureDetailResDto lecture = objectMapper.treeToValue(source, LectureDetailResDto.class);
            lectureList.add(lecture);
        }

        return lectureList;
    }

    @PostConstruct
    @Scheduled(cron = "0 */10 * * * *")
    public void syncLecturesToOpenSearch() {
        List<Lecture> lectures = lectureRepository.findAll();
        for (Lecture lecture : lectures) {
            try {
                registerLecture(lecture.fromEntityToLectureDetailResDto());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
