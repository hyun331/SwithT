package com.tweety.SwithT.lecture.controller;

import com.tweety.SwithT.common.dto.CommonErrorDto;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.dto.*;
import com.tweety.SwithT.lecture.service.LectureService;
import org.springframework.data.web.PageableDefault;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.tweety.SwithT.lecture.dto.LectureSearchDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class LectureController {

    private final LectureService lectureService;

    // 강의 Or 과외 생성
    @PreAuthorize("hasRole('TUTOR')")
    @PostMapping("/create")
    public ResponseEntity<Object> lectureCreate(
            @RequestPart(value = "data") CreateReqDto lectureCreateDto,
            @RequestPart(value = "file", required = false) MultipartFile imgFile) {
        Lecture lecture = lectureService.lectureCreate(lectureCreateDto.getLectureReqDto(), lectureCreateDto.getLectureGroupReqDtos(), imgFile);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "Lecture is successfully created", lecture.getId());
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //과외 또는 강의 리스트
    @GetMapping("/list-of-lecture")
    public ResponseEntity<?> showLectureList(@ModelAttribute LectureSearchDto searchDto, Pageable pageable){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 리스트", lectureService.showLectureList(searchDto, pageable));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //튜터 자신의 과외/강의 리스트
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/my-lecture-list")
    public ResponseEntity<?> showMyLectureList(@ModelAttribute LectureSearchDto searchDto, Pageable pageable){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "나의 강의 리스트", lectureService.showMyLectureList(searchDto, pageable));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //과외/강의 상세화면
    @GetMapping("/lecture-detail/{id}")
    public ResponseEntity<?> lectureDetail(@PathVariable Long id){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 안내 화면", lectureService.lectureDetail(id));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    //과외/관리 수업 관리 화면
    @PreAuthorize("hasRole('TUTOR')")
    @GetMapping("/lecture-class-list/{id}")
    public ResponseEntity<?> showLectureGroupList(@PathVariable Long id, @RequestParam(value = "isAvailable")String isAvailable, Pageable pageable){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의/과외 수업 리스트 화면", lectureService.showLectureGroupList(id, isAvailable, pageable));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

//    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/lectures/{id}/status")
    public ResponseEntity<?> updateLectureStatus(@PathVariable Long id,
                                                 @RequestBody LectureStatusUpdateDto statusUpdateDto) {
        try {
            lectureService.updateLectureStatus(statusUpdateDto);
            CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 상태가 성공적으로 변경되었습니다", null);
            return new ResponseEntity<>(commonResDto, HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            CommonErrorDto commonErrorDto = new CommonErrorDto(HttpStatus.BAD_REQUEST.value(), "강의 정보를 불러오는 데 실패했습니다.");
            return new ResponseEntity<>(commonErrorDto, HttpStatus.BAD_REQUEST);
        }
    }
//    과외 신청자 리스트
//    @PreAuthorize("hasRole('TUTOR')")
//    @GetMapping("/single-lecture-apply-list")
//    public ResponseEntity<?> showSingleLectureApplyList

    // 강의 수정
    @PostMapping("/update/{id}")
    public ResponseEntity<?> lectureUpdate(@PathVariable Long id, @RequestBody LectureUpdateReqDto dto){
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 업데이트", lectureService.lectureUpdate(id, dto));
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 강의 삭제
    //// 강의, 강의 그룹, 그룹 시간 삭제
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> lectureDelete(@PathVariable Long id){
        lectureService.lectureDelete(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 삭제", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 강의 그룹 수정
    @PostMapping("/update/lecture-group/{id}")
    public ResponseEntity<?> lectureGroupUpdate(@PathVariable Long id, @RequestBody LectureGroupReqDto dto){
        lectureService.lectureGroupUpdate(id, dto);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 그룹 업데이트", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }

    // 강의 그룹 삭제
    ////  강의 그룹, 그룹 시간 삭제
    @PostMapping("/delete/lecture-group/{id}")
    public ResponseEntity<?> lectureGroupDelete(@PathVariable Long id){
        lectureService.lectureGroupDelete(id);
        CommonResDto commonResDto = new CommonResDto(HttpStatus.OK, "강의 그룹 삭제", id);
        return new ResponseEntity<>(commonResDto, HttpStatus.OK);
    }


}
