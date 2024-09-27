package com.tweety.SwithT.scheduler.controller;

import com.tweety.SwithT.common.dto.CommonErrorDto;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import com.tweety.SwithT.scheduler.dto.MonthlyScheduleReqDto;
import com.tweety.SwithT.scheduler.dto.ScheduleResDto;
import com.tweety.SwithT.scheduler.dto.ScheduleCreateDto;
import com.tweety.SwithT.scheduler.dto.ScheduleUpdateDto;
import com.tweety.SwithT.scheduler.service.SchedulerService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
public class SchedulerController {

    private final SchedulerService schedulerService;

    public SchedulerController(SchedulerService schedulerService) {
        this.schedulerService = schedulerService;
    }

    @PostMapping("/scheduler/make")
    public ResponseEntity<?> addScheduler(@RequestBody ScheduleCreateDto dto){
        Scheduler scheduler = schedulerService.addSchedule(dto);
        try{
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK,
                    scheduler.getSchedulerDate() + "일" + scheduler.getSchedulerTime() + "에"
                            + scheduler.getTitle() + " 스케줄이 추가되었습니다.", scheduler.getId());
            return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/scheduler/{id}/update")
    public ResponseEntity<?> updateSchedule(@PathVariable Long id, @RequestBody ScheduleUpdateDto dto){
        System.out.println(dto);
        schedulerService.updateSchedule(id, dto);
        try {
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "스케줄이 변경되었습니다.", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.FORBIDDEN.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);
        }
    }

    @PatchMapping("/scheduler/{id}/delete")
    public ResponseEntity<?> deleteSchedule(@PathVariable Long id){
        schedulerService.deleteSchedule(id);
        try{
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "해당 스케줄이 삭제되었습니다.", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);

        } catch (EntityNotFoundException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        } catch (IllegalArgumentException e){
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.FORBIDDEN.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/schedule/{id}")
    public ResponseEntity<?> getScheduleDetail(@PathVariable Long id){
        ScheduleResDto schedule  = schedulerService.getScheduleDetail(id);

        try{
            CommonResDto commonResDto = new CommonResDto(
                    HttpStatus.OK, "조회 성공", schedule);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        }catch (EntityNotFoundException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.NOT_FOUND.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.NOT_FOUND);
        }catch (IllegalArgumentException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(
                    HttpStatus.FORBIDDEN.value(), e.getMessage());
            return new ResponseEntity<>(commonErrorDto, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/schedule/monthly-schedules")
    public ResponseEntity<?> getMonthlySchedule(@RequestBody MonthlyScheduleReqDto dto){
        LocalDate month = LocalDate.of(dto.getYear(), dto.getMonth(), 1);

        // 해당 월의 스케줄 가져오기
        List<ScheduleResDto> schedules = schedulerService.getMonthSchedule(month);

        CommonResDto commonResDto = new CommonResDto(
                HttpStatus.OK, dto.getMonth() + "의 스케줄입니다.", schedules);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
