package com.tweety.SwithT.scheduler.dto;

import com.tweety.SwithT.scheduler.domain.ScheduleAlert;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleAlertCreateDto {

    private Long scheduleId;  // 알림에 대한 스케줄 ID
    private LocalTime reserveTime;  // 예약된 알림 시간
    private LocalDate reserveDay; // 예약된 알림 날짜

    public ScheduleAlert toEntity(Scheduler scheduler){
        return ScheduleAlert.builder()
                .scheduler(scheduler)
                .reserveTime(this.reserveTime.withSecond(0).withNano(0))
                .reserveDay(this.reserveDay)
                .sendYn('N')
                .build();
    }
}
