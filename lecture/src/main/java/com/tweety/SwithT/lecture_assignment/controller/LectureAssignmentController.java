package com.tweety.SwithT.lecture_assignment.controller;

import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.lecture_assignment.dto.create.LectureAssignmentCreateRequest;
import com.tweety.SwithT.lecture_assignment.dto.create.LectureAssignmentCreateResponse;
import com.tweety.SwithT.lecture_assignment.dto.read.LectureAssignmentDetailResponse;
import com.tweety.SwithT.lecture_assignment.dto.read.LectureAssignmentListResponse;
import com.tweety.SwithT.lecture_assignment.dto.update.LectureAssignmentUpdateRequest;
import com.tweety.SwithT.lecture_assignment.dto.update.LectureAssignmentUpdateResponse;
import com.tweety.SwithT.lecture_assignment.repository.LectureAssignmentRepository;
import com.tweety.SwithT.lecture_assignment.service.LectureAssignmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class LectureAssignmentController {
    private final LectureAssignmentService lectureAssignmentService;
    // 생성
    @PostMapping("/lecture/{lectureGroupId}/assignment/create")
    public ResponseEntity<CommonResDto> createAssignment(@PathVariable("lectureGroupId") Long lectureGroupId, @RequestBody LectureAssignmentCreateRequest lectureAssignmentCreateRequest){
        LectureAssignmentCreateResponse lectureAssignmentCreateResponse = lectureAssignmentService.assignmentCreate(lectureGroupId,lectureAssignmentCreateRequest);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.CREATED,"과제가 생성되었습니다.",lectureAssignmentCreateResponse),HttpStatus.CREATED);
    }


    // 목록
    @GetMapping("/lecture/{lectureGroupId}/assignment")
    public ResponseEntity<CommonResDto> assignmentList(@PathVariable("lectureGroupId") Long lectureGroupId, Pageable pageable){
        Page<LectureAssignmentListResponse> lectureAssignmentListResponses = lectureAssignmentService.assignmentList(lectureGroupId,pageable);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"과제 목록이 조회되었습니다.",lectureAssignmentListResponses),HttpStatus.OK);
    }
    // 상세
    @GetMapping("/lecture/assignment/{assignmentId}")
    public ResponseEntity<CommonResDto> assignmentDetail(@PathVariable("assignmentId") Long assignmentId){
        LectureAssignmentDetailResponse lectureAssignmentDetailResponse = lectureAssignmentService.assignmentDetail(assignmentId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"과제 상세 조회되었습니다.",lectureAssignmentDetailResponse),HttpStatus.OK);
    }
    // 수정
    @PutMapping("/lecture/assignment/{lectureAssignmentId}/update")
    public ResponseEntity<CommonResDto> updateAssignment(@PathVariable("lectureAssignmentId") Long lectureAssignmentId, @RequestBody LectureAssignmentUpdateRequest lectureAssignmentUpdateRequest){
        LectureAssignmentUpdateResponse lectureAssignmentUpdateResponse = lectureAssignmentService.assignmentUpdate(lectureAssignmentId,lectureAssignmentUpdateRequest);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"과제가 수정되었습니다.",lectureAssignmentUpdateResponse),HttpStatus.OK);
    }

    // 삭제
    @PatchMapping("/lecture/assignment/{lectureAssignmentId}/delete")
    public ResponseEntity<CommonResDto> deleteAssignment(@PathVariable("lectureAssignmentId") Long lectureAssignmentId){
        String result = lectureAssignmentService.assignmentDelete(lectureAssignmentId);
        return new ResponseEntity<>(new CommonResDto(HttpStatus.OK,"과제가 삭제되었습니다.",result),HttpStatus.OK);
    }
}
