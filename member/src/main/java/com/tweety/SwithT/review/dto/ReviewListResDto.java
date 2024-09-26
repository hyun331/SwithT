package com.tweety.SwithT.review.dto;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewListResDto {

    private BigDecimal star;
    private String title;
    private String contents;
    @JsonFormat(pattern = "yyyy-MM-dd") // 날짜를 "yyyy-MM-dd" 형식으로 변환
    private LocalDateTime createdTime;

}
