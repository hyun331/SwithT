package com.tweety.SwithT.scheduler.dto;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.scheduler.domain.Scheduler;
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
public class ScheduleCreateDto {

    private String title;
    private LocalDate schedulerDate;
    private LocalTime schedulerTime;
    private String content;
    private char alertYn;

    public Scheduler toEntity(Member member){
        return Scheduler.builder()
                .title(this.title)
                .schedulerDate(this.schedulerDate)
                .schedulerTime(this.schedulerTime.withSecond(0).withNano(0))
                .content(this.content)
                .alertYn('N')
                .member(member)
                .build();
    }
}
