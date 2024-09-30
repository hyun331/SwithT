package com.tweety.SwithT.scheduler.dto;

import lombok.*;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class AssignmentCreateReqDto {
    private Long tutorId;
    private List<Long> tuteeList;
    private Long assignmentId;
    private Long lectureGroupId;
    //    private Long memberId;
    private String title;
    private String contents;
    private String schedulerDate;
    private String schedulerTime;
//    @JsonCreator
//    public AssignmentCreateRequest(
//            @JsonProperty("tutorId") Long tutorId,
//            @JsonProperty("tuteeList") List<Long> tuteeList,
//            @JsonProperty("assignmentId") Long assignmentId,
//            @JsonProperty("lectureGroupId") Long lectureGroupId,
//            @JsonProperty("title") String title,
//            @JsonProperty("contents") String contents,
//            @JsonProperty("schedulerDate") LocalDate schedulerDate,
//            @JsonProperty("schedulerTime") LocalTime schedulerTime
//    ) {
//        this.tutorId = tutorId;
//        this.tuteeList = tuteeList;
//        this.assignmentId = assignmentId;
//        this.lectureGroupId = lectureGroupId;
//        this.title = title;
//        this.contents = contents;
//        this.schedulerDate = schedulerDate;
//        this.schedulerTime = schedulerTime;
//    }

}
