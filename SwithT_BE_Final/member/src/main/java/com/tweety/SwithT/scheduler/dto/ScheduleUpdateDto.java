package com.tweety.SwithT.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleUpdateDto {
    private String title;
    private String content;
    private LocalDate schedulerDate;
    private LocalTime schedulerTime;
}
