package com.tweety.SwithT.lecture.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupTimeResDto {
    private Long memberId; // Member ID
    private Long groupTimeId; // GroupTime ID
    private Long lectureGroupId; // LectureGroup ID
    private String lectureDay; // LectureDay (MON, TUE, 등)
    private String lectureType; // 강의 타입(과외, 강의)
    private String startTime; // 강의 시작 시간 (HH:mm)
    private String endTime; // 강의 종료 시간 (HH:mm)
    private String startDate; // 강의 그룹 시작 날짜
    private String endDate; // 강의 그룹 종료 날짜
    private String schedulerTitle;  // 스케줄러에서 사용할 일정 제목
    private char alertYn;           // 알림 여부 ('Y', 'N')
}
