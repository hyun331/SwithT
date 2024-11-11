//package com.tweety.SwithT.comment.controller;
//
//import com.tweety.SwithT.common.service.OpenSearchTestService;
//import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@RestController
//public class TestController {
//    private final OpenSearchTestService openSearchTestService;
//
//    public TestController(OpenSearchTestService openSearchTestService) {
//        this.openSearchTestService = openSearchTestService;
//    }
//
////    @GetMapping("/all")
////    public ResponseEntity<Map<String, Object>> getAllTestLecturesFromOpenSearch() throws IOException, InterruptedException {
////        long startTime = System.currentTimeMillis(); // 요청 시작 시간 측정
////        List<LectureDetailResDto> results = openSearchTestService.searchLecturesWithCodingInTitleFromOpenSearch();
////        long endTime = System.currentTimeMillis(); // 요청 종료 시간 측정
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("result", "DB 조회 결과");
////        response.put("일치하는 수", results.size());
////        response.put("소요 시간", endTime - startTime);
////
////        return ResponseEntity.ok(response);
////    }
////
////    @GetMapping("/db-all")
////    public ResponseEntity<Map<String, Object>> getAllTestLecturesFromDatabase() {
////        long startTime = System.currentTimeMillis(); // 요청 시작 시간 측정
////        List<LectureDetailResDto> results = openSearchTestService.searchLecturesWithCodingInTitleFromDatabase();
////        long endTime = System.currentTimeMillis(); // 요청 종료 시간 측정
////
////        Map<String, Object> response = new HashMap<>();
////        response.put("result", "openSearch 결과");
////        response.put("일치하는 수", results.size());
////        response.put("소요 시간", endTime - startTime);
////
////        return ResponseEntity.ok(response);
////    }
//    @GetMapping("/all")
//    public ResponseEntity<Map<String, Object>> getAllTestLecturesFromOpenSearch(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) throws IOException, InterruptedException {
//
//        long startTime = System.currentTimeMillis();
//        List<LectureDetailResDto> results = openSearchTestService.searchComplexLecturesFromOpenSearch(page, size);
//        long endTime = System.currentTimeMillis();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("조회 대상", "OpenSearch");
//        response.put("일치하는 데이터", results.size());
//        response.put("소요 시간(ms)", endTime - startTime);
//
//        return ResponseEntity.ok(response);
//    }
//
//    @GetMapping("/db-all")
//    public ResponseEntity<Map<String, Object>> getAllTestLecturesFromDatabase(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size) {
//
//        long startTime = System.currentTimeMillis();
//        List<LectureDetailResDto> results = openSearchTestService.searchComplexLecturesFromDatabase(page, size);
//        long endTime = System.currentTimeMillis();
//
//        Map<String, Object> response = new HashMap<>();
//        response.put("조회 대상", "DB");
//        response.put("일치하는 데이터", results.size());
//        response.put("소요 시간(ms)", endTime - startTime);
//
//        return ResponseEntity.ok(response);
//    }
//}
