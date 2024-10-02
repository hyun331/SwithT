package com.tweety.SwithT.lecture_apply.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureApplyAfterResDto {
    //강의 신청 후 리턴해주는 dto
    private Long lectureGroupId;



}
