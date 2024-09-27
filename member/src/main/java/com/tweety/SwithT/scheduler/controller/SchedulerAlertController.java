package com.tweety.SwithT.scheduler.controller;

import com.tweety.SwithT.common.dto.CommonErrorDto;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.scheduler.dto.ScheduleAlertCreateDto;
import com.tweety.SwithT.scheduler.dto.ScheduleAlertUpdateDto;
import com.tweety.SwithT.scheduler.service.SchedulerAlertService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class SchedulerAlertController {

    private final SchedulerAlertService schedulerAlertService;

    @Autowired
    public SchedulerAlertController(SchedulerAlertService schedulerAlertService) {
        this.schedulerAlertService = schedulerAlertService;
    }

    @PostMapping("/scheduler/set-alert")
    public ResponseEntity<?> setAlert(@RequestBody ScheduleAlertCreateDto dto){
        schedulerAlertService.createAndSetAlert(dto);

        try {
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "알림이 설정되었습니다.", dto.getReserveTime());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (IllegalArgumentException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.FORBIDDEN.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);
        } catch (IllegalStateException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.BAD_REQUEST.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/scheduler/cancel-alert/{id}")
    public ResponseEntity<?> cancelAlert(@PathVariable Long id){
        schedulerAlertService.cancelAlert(id);
        try {
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "알림이 취소되었습니다.", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/scheduler/update-alert")
    public ResponseEntity<?> updateAlert(@RequestBody ScheduleAlertUpdateDto dto){
        schedulerAlertService.updateAlert(dto);
        try {
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "알림 정보가 변경되었습니다.",
                    "새로운 알림 시간" + dto.getNewReserveDay() + dto.getNewReserveTime());
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }
}
