package com.tweety.SwithT.lecture_apply.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleLectureApplyListDto {
    private String tuteeName;
    private Long memberId;
    private Long applyId;


}
