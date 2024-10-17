package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.lecture.domain.LectureDay;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTimesResDto {
    private Long groupTimeId;
    private LectureDay lectureDay; // LectureDay (MON, TUE, 등)
    private LocalTime startTime; // 강의 시작 시간 (HH:mm)
    private LocalTime endTime; // 강의 종료 시간 (HH:mm)
}
