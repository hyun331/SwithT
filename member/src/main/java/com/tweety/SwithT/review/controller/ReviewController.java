package com.tweety.SwithT.review.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.review.domain.Review;
import com.tweety.SwithT.review.dto.ReviewListResDto;
import com.tweety.SwithT.review.dto.ReviewReqDto;
import com.tweety.SwithT.review.dto.ReviewUpdateDto;
import com.tweety.SwithT.review.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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


    //리뷰 작성
    @PostMapping("/review/create")
    public ResponseEntity<?> createReview(@RequestBody ReviewReqDto dto) {

        Review review = reviewService.createReview(dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "리뷰 등록 완료", "튜터 id :" + review.getTutorId().getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

    //리뷰 수정
    @PostMapping("/review/update/{id}")
    public ResponseEntity<?> productStockUpdate(@PathVariable Long id, @RequestBody ReviewUpdateDto dto){

        ReviewUpdateDto updateReview = reviewService.updateReview(id,dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "업데이트 성공", updateReview);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);

    }

    //리뷰 리스트 조회 ( 최신순,오래된순,별점높은순,별점낮은순) , postman에서 param에 값 넣어야함
    @GetMapping("/review/list")
    public ResponseEntity<?> getReviews(
            @PageableDefault(size = 5) Pageable pageable,
            @RequestParam(required = false, defaultValue = "createdTime") String sortBy,
            @RequestParam(required = false, defaultValue = "DESC") String order) {
        
        Sort sort;
        if (sortBy.equals("star")) {
            sort = order.equalsIgnoreCase("ASC") ? Sort.by("star").ascending() : Sort.by("star").descending();
        } else if (sortBy.equals("createdTime")) {
            sort = order.equalsIgnoreCase("ASC") ? Sort.by("createdTime").ascending() : Sort.by("createdTime").descending();
        } else {
            // 기본 정렬은 createdTime DESC , Params 값 null인 경우
            sort = Sort.by("createdTime").descending();
        }

        // pageable에 정렬 추가
        pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        Page<ReviewListResDto> reviewListResDtos = reviewService.getReviews(pageable);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "리뷰 리스트 조회 성공", reviewListResDtos);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


}
