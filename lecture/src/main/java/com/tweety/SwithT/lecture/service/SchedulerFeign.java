package com.tweety.SwithT.lecture.service;

import com.tweety.SwithT.common.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name="member-service", configuration = FeignConfig.class)
public interface SchedulerFeign {

}
