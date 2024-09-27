package com.tweety.SwithT.member.controller;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.member.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class MemberLectureController {

    private final MemberService memberService;

    @Autowired
    public MemberLectureController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 강의 상태 변경 API
    @PutMapping("/lectures/{id}/status")
    public ResponseEntity<?> updateLectureStatus(@PathVariable Long id,
                                                 @RequestParam Status newStatus) {
        // 강의 상태 업데이트 처리
        memberService.lectureStatusUpdate(id, newStatus);

        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 상태가 변경되었습니다.", null);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }
}
