package com.tweety.SwithT.lecture_apply.repository;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LectureApplyRepository extends JpaRepository<LectureApply, Long> {

    List<LectureApply> findByMemberIdAndLectureGroup(Long memberId, LectureGroup lectureGroup);

    Page<LectureApply> findByLectureGroup(LectureGroup lectureGroup, Pageable pageable);

    List<LectureApply> findByLectureGroupAndStatus(LectureGroup lectureGroup, Status status);
}
