package com.tweety.SwithT.review.controller;

import com.tweety.SwithT.review.service.ReviewService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

}
