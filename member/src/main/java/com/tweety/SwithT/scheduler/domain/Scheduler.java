package com.tweety.SwithT.scheduler.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.scheduler.dto.AssignmentUpdateReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Scheduler extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate schedulerDate;

    @Column(nullable = false)
    @JsonFormat(pattern = "HH:mm")
    private LocalTime schedulerTime;

    @Column(nullable = false)
    private String content;

    @Column(nullable = true)
    @Builder.Default
    private char alertYn = 'N';

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    private Long lectureGroupId;

    private Long lectureAssignmentId;

    public void deleteSchedule(){
        updateDelYn();
    }

    public void updateSchedule(AssignmentUpdateReqDto dto){
        this.title = dto.getTitle();
        this.content = dto.getContents();
        this.schedulerDate = LocalDate.parse(dto.getSchedulerDate());
        this.schedulerTime = LocalTime.parse(dto.getSchedulerTime());
    }
}
