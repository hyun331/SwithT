package com.tweety.SwithT.tutee.controller;

import com.tweety.SwithT.tutee.service.TuteeService;
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
