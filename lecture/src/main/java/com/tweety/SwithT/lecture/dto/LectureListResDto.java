package com.tweety.SwithT.lecture.dto;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LectureListResDto {
    private Long id;
    private String title;
    private String contents;
    private String memberName;
    private Long memberId;
    private String image;
    private LocalDateTime createdTime;
    private Status status;
    private Boolean isContainsFree;
    private Category category;
}
