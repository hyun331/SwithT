package com.tweety.SwithT.review.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.review.domain.Review;
import com.tweety.SwithT.review.dto.ReviewListResDto;
import com.tweety.SwithT.review.dto.ReviewReqDto;
import com.tweety.SwithT.review.dto.ReviewUpdateDto;
import com.tweety.SwithT.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }


    @PostMapping("/review/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewReqDto dto) {

        Review review = reviewService.createReview(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "리뷰 등록 완료", "튜터 id :" + review.getTutorId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

    //페이징 처리 리스트 반환.
    @GetMapping("/review/list")
    public ResponseEntity<?> getReviews(@PageableDefault(size=5,sort="createdTime",direction=Sort.Direction.DESC)Pageable pageable) {
        Page<ReviewListResDto> reviewListResDtos = reviewService.getReviews(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰 리스트 조회 성공", reviewListResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PutMapping("/review/update/{id}")
    public ResponseEntity<?> productStockUpdate(@PathVariable Long id, ReviewUpdateDto dto){

        Review updateReview = reviewService.updateReview(id,dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "업데이트 성공", updateReview);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }

}
