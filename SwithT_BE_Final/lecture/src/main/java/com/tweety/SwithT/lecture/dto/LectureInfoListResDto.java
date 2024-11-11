package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.lecture.domain.Category;
import com.tweety.SwithT.lecture.domain.LectureType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureInfoListResDto {
    private Long id;
    private String title;
    private String memberName;
    private LectureType lectureType;
    private Category category;
    private String image;
}
