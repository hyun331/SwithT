package com.tweety.SwithT.review.domain;


import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.review.dto.ReviewListResDto;
import com.tweety.SwithT.review.dto.ReviewUpdateDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Review extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "writer_id", nullable = false)
    private Member writerId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tutor_id",nullable = false)
    private Member tutorId;

    @Column(precision = 2, scale = 1, nullable = false)
    private BigDecimal star;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String contents;

    public ReviewListResDto fromEntity(){
        return ReviewListResDto.builder()
                .createdTime(this.getCreatedTime())
                .star(this.star)
                .title(this.title)
                .contents(this.contents)
                .build();
    }

    public void updateReview(ReviewUpdateDto dto){
        this.title = dto.getTitle();
        this.contents = dto.getContents();
    }

}
