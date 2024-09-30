package com.tweety.SwithT.lecture_assignment.dto.create;

import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.board.dto.create.BoardCreateResponse;
import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LectureAssignmentCreateResponse {
    private Long id;
    private Long lectureGroupId;
    private String title;
    private String contents;
    private LocalDate endDate;
    private LocalTime endTime;

    public static LectureAssignmentCreateResponse fromEntity(LectureAssignment lectureAssignment){
        return LectureAssignmentCreateResponse.builder()
                .id(lectureAssignment.getId())
                .lectureGroupId(lectureAssignment.getLectureGroup().getId())
                .contents(lectureAssignment.getContents())
                .title(lectureAssignment.getTitle())
                .endDate(lectureAssignment.getEndDate())
                .endTime(lectureAssignment.getEndTime())
                .build();
    }
}
