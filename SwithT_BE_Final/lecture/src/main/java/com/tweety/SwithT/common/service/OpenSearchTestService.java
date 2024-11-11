//package com.tweety.SwithT.common.service;
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.databind.SerializationFeature;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.tweety.SwithT.lecture.domain.TestLecture;
//import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
//import com.tweety.SwithT.lecture.repository.TestLectureRepository;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.springframework.stereotype.Service;
//import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
//import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
//import software.amazon.awssdk.regions.Region;
//import software.amazon.awssdk.services.s3.S3Client;
//import software.amazon.awssdk.services.s3.model.GetObjectRequest;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.Base64;
//import java.util.List;
//import java.util.Random;
//import java.util.stream.Collectors;
//
//@Service
//public class OpenSearchTestService {
//
//    private final HttpClient client = HttpClient.newHttpClient();
//    private final ObjectMapper objectMapper = new ObjectMapper();
//
//    @Value("${spring.opensearch.url}")
//    private String openSearchUrl;
//
//    @Value("${cloud.aws.credentials.access-key}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String secretKey;
//
//    @Value("${spring.opensearch.region}")
//    private String region;
//
//    @Value("${spring.opensearch.username}")
//    private String username;
//
//    @Value("${spring.opensearch.password}")
//    private String password;
//
//    @Value("${cloud.aws.s3.bucket}")
//    private String bucketName;
//
//    private final TestLectureRepository testLectureRepository;
//
//    @Autowired
//    public OpenSearchTestService(TestLectureRepository testLectureRepository) {
//        this.testLectureRepository = testLectureRepository;
//    }
//
////    @PostConstruct
////    public void init() {
////        try {
////            ensureIndexExists("lecture-test");
////            generateDummyData(20000); // 2만 개의 더미 데이터를 생성 및 등록
////        } catch (IOException | InterruptedException e) {
////            e.printStackTrace();
////        }
////    }
//
//    private void ensureIndexExists(String indexName) throws IOException, InterruptedException {
//        String endpoint = openSearchUrl + "/" + indexName;
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(endpoint))
//                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                .GET()
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() == 404) {
//            createIndex(indexName);
//        }
//    }
//
//    private String downloadIndexConfigFromS3(String bucketName, String key) throws IOException {
//        S3Client s3 = S3Client.builder()
//                .region(Region.of(region))
//                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
//                .build();
//
//        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
//                .bucket(bucketName)
//                .key(key)
//                .build();
//
//        InputStream s3InputStream = s3.getObject(getObjectRequest);
//        try (BufferedReader reader = new BufferedReader(new InputStreamReader(s3InputStream, StandardCharsets.UTF_8))) {
//            StringBuilder content = new StringBuilder();
//            String line;
//            while ((line = reader.readLine()) != null) {
//                content.append(line).append("\n");
//            }
//            return content.toString();
//
//        } catch (Exception e) {
//            throw new IllegalArgumentException("S3에서 인덱스 파일을 찾을 수 없음: " + e.getMessage());
//        }
//    }
//
//    private void createIndex(String indexName) throws IOException, InterruptedException {
//        String endpoint = openSearchUrl + "/" + indexName;
//        String key = "lecture-service/lecture-test.json";
//        String indexMapping = downloadIndexConfigFromS3(bucketName, key);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(endpoint))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                .PUT(HttpRequest.BodyPublishers.ofString(indexMapping))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        if (response.statusCode() != 200) {
//            throw new IOException("인덱스 생성 실패: " + response.body());
//        }
//    }
//
//    public void registerLecture(LectureDetailResDto lecture) throws IOException, InterruptedException {
//        String endpoint = openSearchUrl + "/lecture-test/_doc/" + lecture.getId();
//
//        ObjectMapper localObjectMapper = new ObjectMapper();
//        localObjectMapper.registerModule(new JavaTimeModule());
//        localObjectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        String requestBody = localObjectMapper.writeValueAsString(lecture);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(endpoint))
//                .header("Content-Type", "application/json")
//                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                .PUT(HttpRequest.BodyPublishers.ofString(requestBody))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//        if (response.statusCode() != 200 && response.statusCode() != 201) {
//            throw new IOException("OpenSearch에 강의 등록 실패: " + response.body());
//        }
//    }
//
//    public void generateDummyData(int count) {
//        List<String> topics = List.of("수학", "과학", "코딩", "기계학습", "머신러닝", "데이터 분석", "AI");
//        List<String> descriptions = List.of(
//                "기초부터 고급까지 배우는 과정",
//                "실무에서 활용할 수 있는 강의",
//                "심화 학습을 위한 강의",
//                "초보자를 위한 입문 강의"
//        );
//        List<String> names = List.of("홍길동", "이몽룡", "성춘향", "임꺽정", "김철수");
//
//        Random random = new Random();
//        for (int i = 1; i <= count; i++) {
//            TestLecture testLecture = new TestLecture();
//            testLecture.setTitle(topics.get(random.nextInt(topics.size())) + " 강의 " + i);
//            testLecture.setContents(descriptions.get(random.nextInt(descriptions.size())));
//            testLecture.setMemberName(names.get(random.nextInt(names.size())));
//            testLecture.setSearchCount(random.nextInt(1000));
//            testLecture.setHasFreeGroup(random.nextBoolean() ? "Y" : "N");
//
//            // DB에 저장
//            testLectureRepository.save(testLecture);
//
//            // OpenSearch에 등록
//            try {
//                registerLecture(testLecture.toLectureDetailResDto());
//            } catch (IOException | InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    // OpenSearch의 lecture-test 인덱스에서 모든 데이터를 가져오는 메서드
//    public List<LectureDetailResDto> getAllLecturesFromOpenSearch() throws IOException, InterruptedException {
//        String endpoint = openSearchUrl + "/lecture-test/_search?scroll=1m";
//        String requestBody = """
//                {
//                    "query": {
//                        "match_all": {}
//                    },
//                    "size": 100
//                }
//                """;
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(endpoint))
//                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
//        }
//
//        // 첫 번째 응답에서 scroll_id를 가져옴
//        JsonNode jsonNode = objectMapper.readTree(response.body());
//        String scrollId = jsonNode.path("_scroll_id").asText();
//        List<LectureDetailResDto> lectureList = parseSearchResults(response.body());
//
//        // scroll API를 통해 계속해서 데이터를 가져옴
//        while (true) {
//            String scrollRequestBody = """
//                    {
//                        "scroll": "1m",
//                        "scroll_id": "%s"
//                    }
//                    """.formatted(scrollId);
//
//            HttpRequest scrollRequest = HttpRequest.newBuilder()
//                    .uri(URI.create(openSearchUrl + "/_search/scroll"))
//                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                    .header("Content-Type", "application/json")
//                    .POST(HttpRequest.BodyPublishers.ofString(scrollRequestBody))
//                    .build();
//
//            HttpResponse<String> scrollResponse = client.send(scrollRequest, HttpResponse.BodyHandlers.ofString());
//
//            if (scrollResponse.statusCode() != 200) {
//                throw new IOException("OpenSearch scroll 요청 실패: " + scrollResponse.body());
//            }
//
//            JsonNode scrollNode = objectMapper.readTree(scrollResponse.body());
//            JsonNode hits = scrollNode.path("hits").path("hits");
//
//            if (!hits.isArray() || hits.size() == 0) {
//                break; // 더 이상 가져올 데이터가 없으면 루프 종료
//            }
//
//            lectureList.addAll(parseSearchResults(scrollResponse.body()));
//        }
//
//        return lectureList;
//    }
//
////    public List<LectureDetailResDto> searchLecturesWithCodingInTitleFromOpenSearch() throws IOException, InterruptedException {
////        String endpoint = openSearchUrl + "/lecture-test/_search?scroll=1m";
////        String requestBody = """
////        {
////            "query": {
////                "bool": {
////                    "must": [
////                        { "match": { "title": "코딩" }},
////                        { "range": { "searchCount": { "gte": 500 }}},
////                        { "term": { "hasFreeGroup": "Y" }}
////                    ]
////                }
////            },
////            "size": 1000
////        }
////        """;
////
////        HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(endpoint))
////                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
////                .header("Content-Type", "application/json")
////                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
////                .build();
////
////        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
////
////        if (response.statusCode() != 200) {
////            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
////        }
////
////        // 첫 번째 요청 응답에서 scroll_id 가져오기
////        JsonNode jsonNode = objectMapper.readTree(response.body());
////        String scrollId = jsonNode.path("_scroll_id").asText();
////        List<LectureDetailResDto> lectureList = parseSearchResults(response.body());
////
////        // scroll API를 통해 전체 데이터를 가져오기
////        while (true) {
////            String scrollRequestBody = """
////            {
////                "scroll": "1m",
////                "scroll_id": "%s"
////            }
////            """.formatted(scrollId);
////
////            HttpRequest scrollRequest = HttpRequest.newBuilder()
////                    .uri(URI.create(openSearchUrl + "/_search/scroll"))
////                    .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
////                    .header("Content-Type", "application/json")
////                    .POST(HttpRequest.BodyPublishers.ofString(scrollRequestBody))
////                    .build();
////
////            HttpResponse<String> scrollResponse = client.send(scrollRequest, HttpResponse.BodyHandlers.ofString());
////
////            if (scrollResponse.statusCode() != 200) {
////                throw new IOException("OpenSearch scroll 요청 실패: " + scrollResponse.body());
////            }
////
////            JsonNode scrollNode = objectMapper.readTree(scrollResponse.body());
////            JsonNode hits = scrollNode.path("hits").path("hits");
////
////            if (!hits.isArray() || hits.size() == 0) {
////                break; // 더 이상 가져올 데이터가 없으면 종료
////            }
////
////            lectureList.addAll(parseSearchResults(scrollResponse.body()));
////        }
////
////        return lectureList;
////    }
//
////    public List<LectureDetailResDto> searchLecturesWithCodingInTitleFromOpenSearch(int page) throws IOException, InterruptedException {
////        String endpoint = openSearchUrl + "/lecture-test/_search";
////        int pageSize = 20;
////        int from = page * pageSize;
////
////        String requestBody = """
////        {
////            "query": {
////                "bool": {
////                    "must": [
////                        { "match": { "title": "코딩" }},
////                        { "range": { "searchCount": { "gte": 500 }}},
////                        { "term": { "hasFreeGroup": "Y" }}
////                    ]
////                }
////            },
////            "from": %d,
////            "size": %d
////        }
////        """.formatted(from, pageSize);
////
////        HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(endpoint))
////                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
////                .header("Content-Type", "application/json")
////                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
////                .build();
////
////        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
////
////        if (response.statusCode() != 200) {
////            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
////        }
////
////        return parseSearchResults(response.body());
////    }
//
////    public List<LectureDetailResDto> searchComplexLecturesFromOpenSearch() throws IOException, InterruptedException {
////        String endpoint = openSearchUrl + "/lecture-test/_search?scroll=1m";
////        String requestBody = """
////    {
////        "query": {
////            "bool": {
////                "must": [
////                    { "match": { "title": "코딩" }},
////                    { "match": { "contents": "기초" }},
////                    { "range": { "searchCount": { "gte": 500 }}},
////                    { "term": { "hasFreeGroup": "Y" }}
////                ],
////                "should": [
////                    { "match": { "memberName": "홍길동" }},
////                    { "match": { "contents": "심화" }}
////                ],
////                "must_not": [
////                    { "match": { "title": "고급" }}
////                ]
////            }
////        },
////        "size": 20
////    }
////    """;
////
////        HttpRequest request = HttpRequest.newBuilder()
////                .uri(URI.create(endpoint))
////                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
////                .header("Content-Type", "application/json")
////                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
////                .build();
////
////        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
////
////        if (response.statusCode() != 200) {
////            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
////        }
////
////        // Parse the results from the response
////        List<LectureDetailResDto> lectureList = parseSearchResults(response.body());
////        return lectureList;
////    }
//
//    public List<LectureDetailResDto> searchComplexLecturesFromOpenSearch(int page, int size) throws IOException, InterruptedException {
//        int from = page * size; // 시작 지점 계산
//        String endpoint = openSearchUrl + "/lecture-test/_search";
//        String requestBody = """
//    {
//        "query": {
//            "bool": {
//                "must": [
//                    { "match": { "title": "코딩" }},
//                    { "match": { "contents": "기초" }},
//                    { "range": { "searchCount": { "gte": 500 }}},
//                    { "term": { "hasFreeGroup": "Y" }}
//                ],
//                "should": [
//                    { "match": { "memberName": "홍길동" }},
//                    { "match": { "contents": "심화" }}
//                ],
//                "must_not": [
//                    { "match": { "title": "고급" }}
//                ]
//            }
//        },
//        "from": %d,
//        "size": %d
//    }
//    """.formatted(from, size);
//
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create(endpoint))
//                .header("Authorization", "Basic " + Base64.getEncoder().encodeToString((username + ":" + password).getBytes()))
//                .header("Content-Type", "application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
//                .build();
//
//        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
//
//        if (response.statusCode() != 200) {
//            throw new IOException("OpenSearch 검색 요청 실패: " + response.body());
//        }
//
//        List<LectureDetailResDto> lectureList = parseSearchResults(response.body());
//        return lectureList;
//    }
//
//    private List<LectureDetailResDto> parseSearchResults(String responseBody) throws IOException {
//        List<LectureDetailResDto> lectureList = new ArrayList<>();
//
//        JsonNode jsonNode = objectMapper.readTree(responseBody);
//        JsonNode hits = jsonNode.path("hits").path("hits");
//
//        for (JsonNode hit : hits) {
//            JsonNode source = hit.path("_source");
//            LectureDetailResDto lecture = objectMapper.treeToValue(source, LectureDetailResDto.class);
//            lectureList.add(lecture);
//        }
//
//        return lectureList;
//    }
//
//    // DB에서 같은 조건으로 검색
////    public List<LectureDetailResDto> searchLecturesWithCodingInTitleFromDatabase() {
////        List<TestLecture> testLectures = testLectureRepository.findByTitleContainingAndSearchCountGreaterThanEqualAndHasFreeGroup(
////                "코딩", 500, "Y");
////        return testLectures.stream()
////                .map(TestLecture::toLectureDetailResDto)
////                .collect(Collectors.toList());
////    }
////    public List<LectureDetailResDto> searchLecturesWithCodingInTitleFromDatabase(int page) {
////        int pageSize = 20;
////        Pageable pageable = PageRequest.of(page, pageSize);
////
////        Page<TestLecture> testLectures = testLectureRepository
////                .findByTitleContainingAndSearchCountGreaterThanEqualAndHasFreeGroup(
////                        "코딩", 500, "Y", pageable);
////
////        return testLectures.stream()
////                .map(TestLecture::toLectureDetailResDto)
////                .collect(Collectors.toList());
////    }
//    public List<LectureDetailResDto> searchComplexLecturesFromDatabase(int page, int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        Page<TestLecture> testLectures = testLectureRepository.findByComplexConditions(
//                "코딩",         // 제목에 '코딩' 포함
//                "기초",         // 내용에 '기초' 포함
//                500,           // 최소 조회수 500 이상
//                "Y",           // hasFreeGroup이 'Y'
//                "홍길동",       // memberName이 '홍길동'
//                "심화",         // 추가 조건 내용에 '심화' 포함
//                "고급",         // 제외 조건 제목에 '고급' 포함
//                pageable       // 페이징 매개변수
//        );
//
//        return testLectures.stream()
//                .map(TestLecture::toLectureDetailResDto)
//                .collect(Collectors.toList());
//    }
//}
