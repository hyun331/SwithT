package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.common.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TuteeMyLectureListResDto {
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long applyId;
    private String tutorName;
    private Long lectureGroupId;
    private Status status;
    private int price;

}
