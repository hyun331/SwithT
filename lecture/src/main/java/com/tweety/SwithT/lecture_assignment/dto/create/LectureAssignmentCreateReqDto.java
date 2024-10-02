package com.tweety.SwithT.lecture_assignment.dto.create;

import com.tweety.SwithT.lecture.domain.LectureGroup;
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
public class LectureAssignmentCreateReqDto {
    private String title;
    private String contents;
    private LocalDate endDate;
    private LocalTime endTime;
    public static LectureAssignment toEntity(LectureGroup lectureGroup, LectureAssignmentCreateReqDto dto){
        return LectureAssignment.builder()
                .lectureGroup(lectureGroup)
                .contents(dto.getContents())
                .title(dto.getTitle())
                .endDate(dto.getEndDate())
                .endTime(dto.getEndTime())
                .build();
    }
}
