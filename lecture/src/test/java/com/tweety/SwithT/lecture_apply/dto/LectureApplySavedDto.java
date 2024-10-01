package com.tweety.SwithT.lecture_apply.dto;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;


public class LectureApplySavedDto {
    //과외 신청 양식
    private Long lectureGroupId;

    public void setLectureGroupId(Long lectureGroupId) {
        this.lectureGroupId = lectureGroupId;
    }

    public Long getLectureGroupId() {
        return this.lectureGroupId;
    }

    public LectureApply toEntity(LectureGroup lectureGroup, Long memberId, String memberName){
        return LectureApply.builder()
                .lectureGroup(lectureGroup)
                .memberId(memberId)
                .memberName(memberName)
                .status(Status.STANDBY)
                .build();
    }
}
