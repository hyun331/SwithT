package com.tweety.scheduler.controller;

import com.tweety.scheduler.service.SchedulerService;
import org.springframework.stereotype.Controller;

@Controller
public class SchedulerController {

    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }
}
