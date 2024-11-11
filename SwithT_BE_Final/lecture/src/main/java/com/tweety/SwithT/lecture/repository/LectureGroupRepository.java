package com.tweety.SwithT.lecture.repository;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureGroupRepository extends JpaRepository<LectureGroup, Long> {

    List<LectureGroup> findByLectureId(Long lectureId);

    List<LectureGroup> findByLectureIdAndDelYn(Long lectureId, String delYn);

    Optional<LectureGroup> findByIdAndIsAvailable(Long lectureGroupId, String isAvailable);

    Page<LectureGroup> findAll(Specification<LectureGroup> specification, Pageable pageable);

    Optional<LectureGroup> findByIdAndDelYn(Long lectureGroupId, String delYn);

    // lectureId로 상위 5개 board 리스트 가져오기
    @Query("SELECT p FROM Board p WHERE p.lectureGroup.lecture.id = :lectureId AND p.delYn = 'N' ORDER BY p.createdTime DESC")
    List<Board> findTop5BoardsByLectureId(@Param("lectureId") Long lectureId, Pageable pageable);


    // lectureId로 상위 5개 assignment 리스트 가져오기 (endDate가 가장 임박한 순서로 정렬)
    @Query("SELECT p FROM LectureAssignment p WHERE p.lectureGroup.lecture.id = :lectureId AND p.delYn = 'N' AND p.endDate > CURRENT_TIMESTAMP ORDER BY p.endDate ASC")
    List<LectureAssignment> findTop5AssignmentsByLectureId(@Param("lectureId") Long lectureId, Pageable pageable);



}
