package com.tweety.SwithT.review.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.review.domain.Review;
import com.tweety.SwithT.review.dto.ReviewReqDto;
import com.tweety.SwithT.review.service.ReviewService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/review/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewReqDto dto) {

        Review review = reviewService.createReview(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "리뷰 등록 완료", "튜터 id :"+ review.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

}
