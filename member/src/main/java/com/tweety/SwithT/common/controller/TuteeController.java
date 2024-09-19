package com.tweety.SwithT.common.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TuteeController {

    private final TuteeService tuteeService;

    @Autowired
    public TuteeController(TuteeService tuteeService) {
        this.tuteeService = tuteeService;
    }


}
