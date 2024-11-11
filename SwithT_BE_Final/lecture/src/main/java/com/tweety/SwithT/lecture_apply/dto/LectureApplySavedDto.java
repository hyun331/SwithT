package com.tweety.SwithT.lecture_apply.dto;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LectureApplySavedDto {
    //강의 신청 양식
    private Long lectureGroupId;

    public LectureApply toEntity(LectureGroup lectureGroup, Long memberId, String memberName){
        return LectureApply.builder()
                .lectureGroup(lectureGroup)
                .memberId(memberId)
                .memberName(memberName)
                .status(Status.STANDBY)
                .build();
    }
}
