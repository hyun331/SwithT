package com.tweety.SwithT.member.dto;

import com.tweety.SwithT.common.domain.Category;
import com.tweety.SwithT.common.domain.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureDto {
    private String title;
    private String contents;
    private String image;
    private Long memberId;
    private String memberName;
    private Status status;
    private Category category;
}
