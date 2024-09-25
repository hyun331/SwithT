package com.tweety.scheduler.service;

import com.tweety.scheduler.repository.SchedulerRepository;
import org.springframework.stereotype.Service;

@Service
public class SchedulerService {

    private final SchedulerRepository schedulerRepository;

    public SchedulerService(SchedulerRepository schedulerRepository) {
        this.schedulerRepository = schedulerRepository;
    }

}
