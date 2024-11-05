package com.tweety.SwithT.scheduler.repository;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler,Long> {
    List<Scheduler> findAllByLectureAssignmentIdAndMemberId(Long lectureAssignmentId, Long memberId);
    List<Scheduler> findByLectureAssignmentId(Long lectureAssignmentId);
    List<Scheduler> findAllByMemberAndSchedulerDateBetweenAndDelYn(
            Member member, LocalDate startDate, LocalDate endDate, String delYn);
    List<Scheduler> findAllByLectureGroupId(Long lectureGroupId);
    List<Scheduler> findAllByMemberIdAndLectureGroupId(Long memberId, Long lectureGroupId);
    List<Scheduler> findAllByMemberIdAndLectureGroupIdAndGroupTimeId(Long memberId, Long lectureGroupId, Long groupTimeId);
}
