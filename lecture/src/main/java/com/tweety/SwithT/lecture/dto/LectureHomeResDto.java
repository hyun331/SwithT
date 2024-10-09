package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureHomeResDto {

    // 강의 정보
    private Long lectureId;
    private String title;
    private String contents;
    private String image;
    private Long memberId;
    private String memberName;
    private Category category;
    
    // 강의 그룹 정보
    private Long groupId;
    private Integer limitPeople;

    private String latitude;

    private String longitude;

    private LocalDate startDate;

    // 강의 그룹 일정
    @Builder.Default
    private List<GroupTimeResDto> lectureGroupTimes = new ArrayList<>();

    // 단체 채팅방
    private Long chatRoomId;
}
