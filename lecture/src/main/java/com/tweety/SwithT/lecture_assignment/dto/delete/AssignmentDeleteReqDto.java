package com.tweety.SwithT.lecture_assignment.dto.delete;

import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentDeleteReqDto {
    private Long assignmentId;

    public static AssignmentDeleteReqDto fromEntity(LectureAssignment assignment){
        return AssignmentDeleteReqDto.builder()
                .assignmentId(assignment.getId())
                .build();
    }
}
