package com.tweety.SwithT.lecture_apply.repository;

import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureApplyRepository extends JpaRepository<LectureApply, Long> {

    List<LectureApply> findByMemberIdAndLectureGroup(Long memberId, LectureGroup lectureGroup);

    Optional<LectureApply> findByLectureGroupId(Long lectureGroupId);
    @Query("SELECT la FROM LectureApply la WHERE la.lectureGroup.id = :lectureGroupId AND la.status = 'ADMIT'")
    List<LectureApply> findMemberIdsByLectureGroupIdAndStatusAdmit(@Param("lectureGroupId") Long lectureGroupId);

}
