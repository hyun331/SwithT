package com.tweety.SwithT.lecture_assignment.dto.create;

import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentCreateReqDto {
    private Long tutorId;
    private List<Long> tuteeList;
    private Long assignmentId;
    private Long lectureGroupId;
    //    private Long memberId;
    private String title;
    private String contents;
    private LocalDate schedulerDate;
    private LocalTime schedulerTime;

    public static AssignmentCreateReqDto fromEntity(Long lectureGroupId, Long tutorId, List<Long> tuteeList, LectureAssignment assignment){
        return AssignmentCreateReqDto.builder()
                .tutorId(tutorId)
                .tuteeList(tuteeList)
                .assignmentId(assignment.getId())
                .lectureGroupId(lectureGroupId)
                .title(assignment.getTitle())
                .contents(assignment.getContents())
                .schedulerDate(assignment.getEndDate())
                .schedulerTime(assignment.getEndTime())
                .build();
    }
}
