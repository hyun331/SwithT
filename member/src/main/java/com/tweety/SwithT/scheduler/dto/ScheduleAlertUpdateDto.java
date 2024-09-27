package com.tweety.SwithT.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleAlertUpdateDto {

    private Long scheduleId;  // 알림에 대한 스케줄 ID
    private LocalTime newReserveTime;  // 예약된 알림 시간
    private LocalDate newReserveDay; // 예약된 알림 날짜

}
