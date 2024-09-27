package com.tweety.SwithT.lecture_apply.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.lecture_apply.dto.SingleLectureApplySavedDto;
import com.tweety.SwithT.lecture_apply.service.LectureApplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LectureApplyController {

    private final LectureApplyService lectureApplyService;


    //과외 신청
    @PreAuthorize("hasRole('TUTEE')")
    @PostMapping("/single-lecture-apply")
    public ResponseEntity<?> tuteeSingleLectureApply(@RequestBody SingleLectureApplySavedDto dto){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.CREATED, "튜티의 과외 신청 완료", lectureApplyService.tuteeSingleLectureApply(dto));
        return new ResponseEntity<>(commonResDto, HttpStatus.CREATED);

    }

    //    과외 신청자 리스트. id는 강의 그룹
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/single-lecture-apply-list/{id}")
    public ResponseEntity<?> showSingleLectureApplyList(@PathVariable Long id, Pageable pageable){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "튜티의 과외 신청자 리스트", lectureApplyService.singleLectureApplyList(id, pageable));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //튜터 - 매칭할 튜티에게 결제 요청 보내기. id는 lecture_apply의 id
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/single-lecture-payment-request/{id}")
    public ResponseEntity<?> singleLecturePaymentRequest(@PathVariable Long id){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "튜터가 튜티에게 결제 요청", lectureApplyService.singleLecturePaymentRequest(id));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    @PreAuthorize("hasRole('TUTOR')")
    @PatchMapping("/single-lecture-apply-reject/{id}")
    public ResponseEntity<?> singleLectureApplyReject(@PathVariable Long id){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "과외 수강신청 거절", lectureApplyService.singleLectureApplyReject(id));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

}
