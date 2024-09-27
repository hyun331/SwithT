package com.tweety.SwithT.review.service;


import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import com.tweety.SwithT.review.domain.Review;
import com.tweety.SwithT.review.dto.ReviewListResDto;
import com.tweety.SwithT.review.dto.ReviewReqDto;
import com.tweety.SwithT.review.dto.ReviewUpdateDto;
import com.tweety.SwithT.review.repository.ReviewRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Transactional
@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, MemberRepository memberRepository) {
        this.reviewRepository = reviewRepository;
        this.memberRepository = memberRepository;
    }

    public Review createReview(ReviewReqDto dto) {

        Member writerId = memberRepository
                .findById(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보 입니다."));

        Member tutorId = memberRepository.findById(dto.getTutorId())
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 튜터 정보 입니다."));

        Review review = dto.toEntity(writerId,tutorId);
        reviewRepository.save(review);

        return review;
    }

    @Transactional(readOnly = true)
    public Page<ReviewListResDto> getReviews(Pageable pageable) {
        Page<Review> reviews = reviewRepository.findAll(pageable);
        return reviews.map(Review::fromEntity);
    }

    public ReviewUpdateDto updateReview(Long id, ReviewUpdateDto updateDto) {

        Member member = memberRepository
                .findById(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보 입니다."));

        Review review = reviewRepository
                .findById(id).orElseThrow(() -> new EntityNotFoundException("존재하지 않는 리뷰 입니다."));

        if ( !review.getWriterId().getId().equals(member.getId())) {
            throw new IllegalArgumentException("본인의 리뷰만 수정할 수 있습니다.");
        }

        review.updateReview(updateDto);
        return updateDto;
    }

    @Transactional
    public void updateAvgScores() {

        List<Member> tutors = memberRepository.findAll(); // 모든 튜터를 가져오면 됩니다.
        for (Member tutor : tutors) {
            // 튜터의 평균 점수를 계산
            BigDecimal avgScore = reviewRepository.findAverageStarByTutorId(tutor.getId());
            if (avgScore != null) {
                tutor.setAvgScore(avgScore);  // setAvgScore는 @Setter가 필요합니다.
                memberRepository.save(tutor);  // 평균 점수를 업데이트
            }
        }
    }

}
