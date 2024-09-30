package com.tweety.SwithT.lecture_assignment.dto.delete;

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
public class AssignmentDeleteRequest {
    private Long assignmentId;

    public static AssignmentDeleteRequest fromEntity(LectureAssignment assignment){
        return AssignmentDeleteRequest.builder()
                .assignmentId(assignment.getId())
                .build();
    }
}
