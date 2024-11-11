package com.tweety.SwithT.lecture_assignment.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_assignment.dto.update.LectureAssignmentUpdateReqDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LectureAssignment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_group_id")
    private LectureGroup lectureGroup;

    @Column(nullable = false)
    private String title;

    private String contents;

    // 과제 마감일
    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
    // 과제 마감 시간
    @Column(nullable = false)
    private LocalTime endTime;

    public void updateAssignment(LectureAssignmentUpdateReqDto dto) {
        this.title = dto.getTitle();
        this.contents = dto.getContents();
        this.endDate = dto.getEndDate();
        this.endTime = dto.getEndTime();
    }
}
