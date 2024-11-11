//package com.tweety.SwithT.lecture.repository;
//
//import com.tweety.SwithT.lecture.domain.TestLecture;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//import org.springframework.data.repository.query.Param;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//@Repository
//public interface TestLectureRepository extends JpaRepository<TestLecture, Long> {
//    List<TestLecture> findByTitleContaining(String title);
//    List<TestLecture> findByTitleContainingAndSearchCountGreaterThanEqualAndHasFreeGroup(String title, int searchCount, String hasFreeGroup);
//    Page<TestLecture> findByTitleContainingAndSearchCountGreaterThanEqualAndHasFreeGroup(
//            String title, int searchCount, String hasFreeGroup, Pageable pageable);
//
//    @Query("SELECT t FROM TestLecture t WHERE " +
//            "t.title LIKE %:title% AND " +
//            "t.contents LIKE %:contents% AND " +
//            "t.searchCount >= :minSearchCount AND " +
//            "t.hasFreeGroup = :hasFreeGroup AND " +
//            "(t.memberName = :memberName OR t.contents LIKE %:additionalContent%) AND " +
//            "t.title NOT LIKE %:excludeTitle%")
//    Page<TestLecture> findByComplexConditions(
//            @Param("title") String title,
//            @Param("contents") String contents,
//            @Param("minSearchCount") int minSearchCount,
//            @Param("hasFreeGroup") String hasFreeGroup,
//            @Param("memberName") String memberName,
//            @Param("additionalContent") String additionalContent,  // 추가 조건으로 '심화' 포함
//            @Param("excludeTitle") String excludeTitle,  // 제외할 조건인 '고급' 포함
//            Pageable pageable // 페이징을 위한 매개변수
//    );
//}
