package com.tweety.SwithT.review.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
public class ReviewScheduler {

    private final ReviewService reviewService;

    @Autowired
    public ReviewScheduler(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    //    @Scheduled(cron = "0 0/1 * * * *")
    @Scheduled(cron = "*/5 * * * * *") // 5초마다 갱신하는 것
    @Transactional
    public void postSchedule() {
        System.out.println("평점 업데이트 스케줄러 시작");
        reviewService.updateAvgScores();
    }

}
