package com.tweety.SwithT.lecture_assignment.repository;

import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface LectureAssignmentRepository extends JpaRepository<LectureAssignment, Long> {
    @Query("SELECT la.memberId FROM LectureApply la WHERE la.lectureGroup.id = :lectureGroupId AND la.status = 'ADMIT'")
    List<Long> findMemberIdsByLectureGroupIdAndStatusAdmit(@Param("lectureGroupId") Long lectureGroupId);
    Page<LectureAssignment> findByLectureGroupIdAndDelYn(@Param("lectureGroupId") Long lectureGroupId, @Param("delYn")String delYn, Pageable pageable);
}
