package com.tweety.SwithT.scheduler.repository;

import com.tweety.SwithT.scheduler.domain.ScheduleAlert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface SchedulerAlertRepository extends JpaRepository<ScheduleAlert, Long> {

    // 현재 시간과 예약 시간 사이의 알림들 가져오기
    List<ScheduleAlert> findByReserveTimeBetween(LocalTime start, LocalTime end);

}