package com.tweety.SwithT.review.service;


import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ReviewService {
    private final Repository repository;

    public ReviewService(Repository repository) {
        this.repository = repository;
    }


}
