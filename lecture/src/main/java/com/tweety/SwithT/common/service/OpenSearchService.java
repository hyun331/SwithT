package com.tweety.SwithT.common.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
import jakarta.annotation.PostConstruct;
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
import java.util.Base64;
import java.util.List;

@Service
public class OpenSearchService {

    private final HttpClient client = HttpClient.newHttpClient(); // HTTP 클라이언트 초기화
    private final ObjectMapper objectMapper = new ObjectMapper(); // JSON 변환을 위한 ObjectMapper 초기화

    // OpenSearch 연결 정보 및 AWS 인증 정보
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

    // OpenSearch 클라이언트 생성 메서드 - AWS 인증 정보를 기반으로 OpenSearch 클라이언트 생성
    public OpenSearchClient createOpenSearchClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return OpenSearchClient.builder()
                .region(Region.of(region)) // 리전 설정
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds)) // 자격 증명 설정
                .endpointOverride(URI.create(openSearchUrl)) // OpenSearch 엔드포인트 설정
                .build();
    }

    // 애플리케이션 시작 시 인덱스가 존재하는지 확인하고 없으면 생성하는 메서드를 호출
    @PostConstruct
    public void init() {
        try {
            ensureIndexExists("lecture-service");  // "lecture-service"라는 인덱스 확인 및 생성
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 인덱스가 존재하는지 확인하고 없으면 생성하는 메서드
    private void ensureIndexExists(String indexName) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/" + indexName;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes())) // 인증 헤더 추가
                .GET() // GET 요청으로 인덱스 확인
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 404) { // 인덱스가 없으면 생성
            createIndex(indexName);
        }
    }

    // OpenSearch에 인덱스를 생성하는 메서드
    private void createIndex(String indexName) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/" + indexName;

        // 인덱스 설정 및 매핑 - ngram을 사용하여 단어를 부분적으로 검색할 수 있도록 설정
        String indexMapping = """
        {
            "settings": {
                "index": {
                    "max_ngram_diff": 14 // ngram의 최대 차이를 설정 (min_gram과 max_gram의 차이 허용)
                },
                "analysis": {
                    "tokenizer": {
                        "ngram_tokenizer": {
                            "type": "ngram", // ngram 방식의 토크나이저 설정
                            "min_gram": 1,   // 최소 ngram 길이 설정
                            "max_gram": 15,  // 최대 ngram 길이 설정
                            "token_chars": [
                                "letter",
                                "digit"  // 문자와 숫자를 토큰으로 처리
                            ]
                        }
                    },
                    "analyzer": {
                        "ngram_analyzer": {
                            "tokenizer": "ngram_tokenizer", // ngram 토크나이저를 사용하는 분석기
                            "filter": [
                                "lowercase" // 소문자로 변환 필터 추가
                            ]
                        }
                    }
                }
            },
            "mappings": {
                "properties": {
                    "title": {
                        "type": "text",
                        "analyzer": "ngram_analyzer" // title 필드에 ngram 분석기 적용
                    },
                    "contents": {
                        "type": "text",
                        "analyzer": "ngram_analyzer", // contents 필드에 ngram 분석기 적용
                        "fields": {
                            "ngram": {
                                "type": "text",
                                "analyzer": "ngram_analyzer"
                            }
                        }
                    },
                    "memberName": {
                        "type": "text",
                        "analyzer": "ngram_analyzer" // memberName 필드에 ngram 분석기 적용
                    }
                }
            }
        }
        """;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .PUT(HttpRequest.BodyPublishers.ofString(indexMapping)) // PUT 요청으로 인덱스 생성
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("인덱스 생성 실패: " + response.body());
        }
    }

    // OpenSearch에 강의를 등록하는 메서드
    public void registerLecture(LectureDetailResDto lecture) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_doc/" + lecture.getId(); // 강의 ID에 따라 문서 추가
        String requestBody = objectMapper.writeValueAsString(lecture); // LectureDetailResDto 객체를 JSON으로 변환

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Content-Type", "application/json")
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes())) // 인증 헤더 추가
                .PUT(HttpRequest.BodyPublishers.ofString(requestBody)) // 문서 등록 요청
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200 && response.statusCode() != 201) {
            throw new IOException("OpenSearch에 강의 등록 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의를 삭제하는 메서드
    public void deleteLecture(Long lectureId) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_doc/" + lectureId; // 강의 ID로 문서 삭제

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .DELETE() // DELETE 요청으로 문서 삭제
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("OpenSearch에서 강의 삭제 실패: " + response.body());
        }
    }

    // OpenSearch에서 강의를 검색하는 메서드
    public List<LectureDetailResDto> searchLectures(String keyword, Pageable pageable) throws IOException, InterruptedException {
        String endpoint = openSearchUrl + "/lecture-service/_search"; // 검색 엔드포인트
        String requestBody = String.format("""
    {
        "query": {
            "bool": {
                "must": [
                    {
                        "multi_match": {
                            "query": "%s", // 검색어 설정
                            "fields": ["title", "contents", "memberName"], // 검색할 필드들
                            "type": "best_fields", // 가장 적합한 필드를 기반으로 검색
                            "fuzziness": "AUTO" // 유사도 검색 허용
                        }
                    },
                    {
                        "term": {
                            "delYn": "N" // delYn 값이 N인 경우만 검색
                        }
                    }
                ]
            }
        },
        "from": %d, // 페이지 시작점
        "size": %d  // 페이지 크기
    }
    """, keyword, pageable.getOffset(), pageable.getPageSize());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(endpoint))
                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody)) // POST 요청으로 검색
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() == 200) {
            return parseSearchResults(response.body()); // 검색 결과를 파싱하여 반환
        } else {
            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
        }
    }

    // OpenSearch 응답을 LectureDetailResDto 리스트로 변환하는 메서드
    private List<LectureDetailResDto> parseSearchResults(String responseBody) throws IOException {
        List<LectureDetailResDto> lectureList = new ArrayList<>();
        JsonNode jsonNode = objectMapper.readTree(responseBody); // 응답 JSON을 파싱
        JsonNode hits = jsonNode.path("hits").path("hits"); // 검색 결과에서 hits 부분만 추출

        // 검색된 각 문서를 LectureDetailResDto로 변환하여 리스트에 추가
        for (JsonNode hit : hits) {
            JsonNode source = hit.path("_source");
            LectureDetailResDto lecture = objectMapper.treeToValue(source, LectureDetailResDto.class);
            lectureList.add(lecture);
        }

        return lectureList; // 최종 검색 결과 반환
    }
}
