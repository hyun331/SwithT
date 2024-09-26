
package com.tweety.SwithT.review.dto;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.review.domain.Review;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReqDto {

    private Long tutorId;
    private BigDecimal star;
    private String title;
    private String contents;

    public Review toEntity(Member writerId,Member tutorId) {

        return Review.builder()
                .tutorId(tutorId)
                .writerId(writerId)
                .star(this.star)
                .title(this.title)
                .contents(this.contents)
                .build();
    }

}