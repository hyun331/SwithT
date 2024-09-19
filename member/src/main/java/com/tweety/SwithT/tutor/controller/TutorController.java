package com.tweety.SwithT.tutor.controller;

import com.tweety.SwithT.tutor.service.TutorService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TutorController {

    private final TutorService tutorService;

    public TutorController(TutorService tutorService) {
        this.tutorService = tutorService;
    }

}
