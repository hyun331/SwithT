package com.tweety.SwithT.scheduler.controller;

import com.tweety.SwithT.scheduler.service.SchedulerService;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SchedulerController {

    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
