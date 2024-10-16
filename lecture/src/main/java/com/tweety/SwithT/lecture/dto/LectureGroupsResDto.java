package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.lecture.domain.GroupTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LectureGroupsResDto {
    private Long lectureGroupId;
    private List<GroupTime> groupTimes;
    private String isAvailable;
    private int remaining;
    private int price;
    private String address;
    private LocalDate startDate;
    private LocalDate endDate;

}
