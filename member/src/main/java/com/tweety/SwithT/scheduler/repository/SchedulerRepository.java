package com.tweety.SwithT.scheduler.repository;

import com.tweety.SwithT.scheduler.domain.Scheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SchedulerRepository extends JpaRepository<Scheduler,Long> {
    List<Scheduler> findByLectureAssignmentId(Long lectureAssignmentId);
}
