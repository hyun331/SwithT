package com.tweety.SwithT.lecture_assignment.dto.read;

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
public class LectureAssignmentListResDto {
    private Long id;
    private String title;
    private LocalDate endDate;
    private LocalTime endTime;

    public static LectureAssignmentListResDto fromEntity(LectureAssignment lectureAssignment){
        return LectureAssignmentListResDto.builder()
                .id(lectureAssignment.getId())
                .title(lectureAssignment.getTitle())
                .endDate(lectureAssignment.getEndDate())
                .endTime(lectureAssignment.getEndTime())
                .build();
    }
}
