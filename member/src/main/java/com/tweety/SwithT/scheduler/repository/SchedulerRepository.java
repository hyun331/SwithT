package com.tweety.SwithT.scheduler.repository;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;


@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler,Long> {
    List<Scheduler> findByLectureAssignmentId(Long lectureAssignmentId);
    List<Scheduler> findAllByMemberAndSchedulerDateBetween(Member member,LocalDate startOfMonth, LocalDate endOfMonth);
}
