package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.Category;
import com.tweety.SwithT.lecture.domain.LectureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LectureResDto {
    private Long id;
    private String title;
    private String contents;
    private String image;
    private Long memberId;
    private String memberName;
    private Status status;
    private Category category;
    private LectureType lectureType;
}
