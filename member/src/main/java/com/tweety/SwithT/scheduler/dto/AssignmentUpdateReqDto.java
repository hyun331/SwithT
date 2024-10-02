package com.tweety.SwithT.scheduler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentUpdateReqDto {
    private Long tutorId;
    private List<Long> tuteeList;
    private Long assignmentId;
    private Long lectureGroupId;
    //    private Long memberId;
    private String title;
    private String contents;
    private String schedulerDate;
    private String schedulerTime;

}
