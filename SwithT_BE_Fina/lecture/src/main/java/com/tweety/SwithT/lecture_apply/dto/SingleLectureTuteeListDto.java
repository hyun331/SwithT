package com.tweety.SwithT.lecture_apply.dto;

import com.tweety.SwithT.common.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SingleLectureTuteeListDto {
    private String tuteeName;
    private String tuteeProfile;
    private Long memberId;
}
