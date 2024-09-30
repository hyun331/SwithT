package com.tweety.SwithT.lecture_assignment.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.board.domain.Board;
import com.tweety.SwithT.board.dto.create.BoardCreateRequest;
import com.tweety.SwithT.common.service.MemberFeign;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import com.tweety.SwithT.lecture_apply.repository.LectureApplyRepository;
import com.tweety.SwithT.lecture_assignment.domain.LectureAssignment;
import com.tweety.SwithT.lecture_assignment.dto.create.AssignmentCreateRequest;
import com.tweety.SwithT.lecture_assignment.dto.create.LectureAssignmentCreateRequest;
import com.tweety.SwithT.lecture_assignment.dto.create.LectureAssignmentCreateResponse;
import com.tweety.SwithT.lecture_assignment.dto.delete.AssignmentDeleteRequest;
import com.tweety.SwithT.lecture_assignment.dto.read.LectureAssignmentDetailResponse;
import com.tweety.SwithT.lecture_assignment.dto.read.LectureAssignmentListResponse;
import com.tweety.SwithT.lecture_assignment.dto.update.AssignmentUpdateRequest;
import com.tweety.SwithT.lecture_assignment.dto.update.LectureAssignmentUpdateRequest;
import com.tweety.SwithT.lecture_assignment.dto.update.LectureAssignmentUpdateResponse;
import com.tweety.SwithT.lecture_assignment.repository.LectureAssignmentRepository;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class LectureAssignmentService {
    private final LectureAssignmentRepository lectureAssignmentRepository;
    private final LectureGroupRepository lectureGroupRepository;
    private final MemberFeign memberFeign;
    private final LectureApplyRepository lectureApplyRepository;
    private final ObjectMapper objectMapper;
    private final KafkaTemplate kafkaTemplate;
    public LectureAssignmentService(LectureAssignmentRepository lectureAssignmentRepository, LectureGroupRepository lectureGroupRepository, MemberFeign memberFeign, LectureApplyRepository lectureApplyRepository, ObjectMapper objectMapper, KafkaTemplate kafkaTemplate) {
        this.lectureAssignmentRepository = lectureAssignmentRepository;
        this.lectureGroupRepository = lectureGroupRepository;
        this.memberFeign = memberFeign;
        this.lectureApplyRepository = lectureApplyRepository;
        this.objectMapper = objectMapper;
        this.kafkaTemplate = kafkaTemplate;
    }
    public LectureAssignmentCreateResponse assignmentCreate(Long lectureGroupId,LectureAssignmentCreateRequest lectureAssignmentCreateRequest){
        // lecturegroup에서 lecture 정보 -> 현재 로그인 회원이 tutor인지 체크
        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId).orElseThrow(()->new EntityNotFoundException("해당 그룹이 없습니다."));
        Lecture lecture = lectureGroup.getLecture();
        Long lectureTutorId = lecture.getMemberId();
        Long loginMemberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!lectureTutorId.equals(loginMemberId)) throw new RuntimeException("튜터만 과제를 생성할 수 있습니다.");

        // 과제 생성
        LectureAssignment lectureAssignment = lectureAssignmentRepository.save(LectureAssignmentCreateRequest.toEntity(lectureGroup,lectureAssignmentCreateRequest));
        // dto로 만들어주기....
        // 과제 튜터 튜티한테 일정 생성
        // 튜터 튜티 전체 리스트를 어떻게?
        // 튜티 : 강의 그룹 - 강의 신청(lecture_apply -- status admit인 memberId)
        // findbygroupid list로 받기 - memberid만...
        List<LectureApply> lectureApplies = lectureApplyRepository.findMemberIdsByLectureGroupIdAndStatusAdmit(lectureGroupId);
        List<Long> tuteeList = new ArrayList<>();
        for(LectureApply lectureApply : lectureApplies){
            tuteeList.add(lectureApply.getMemberId());
        }
        // 튜터 : 강의 그룹 - 강의 id - 강의의 member id
        Long tutorId = lecture.getMemberId();
        // feign으로 강의 그룹에 해당되어 있는 튜터 튜티 모두에게 주어야함
        AssignmentCreateRequest dto = AssignmentCreateRequest.fromEntity(lectureGroupId,tutorId,tuteeList,lectureAssignment);
        try {
            String message = objectMapper.writeValueAsString(dto);
//            kafkaTemplate.send("assignment-create", dto);

            kafkaTemplate.send("assignment-create", message);  // JSON 문자열 전송

            System.out.println("Kafka 메시지 전송됨: " + message);
        } catch (JsonProcessingException e) {
            System.err.println("Kafka 메시지 변환 및 0전송 실패: " + e.getMessage());
        }
        // 과제 결과값 리턴
        return LectureAssignmentCreateResponse.fromEntity(lectureAssignment);

    }
    public Page<LectureAssignmentListResponse> assignmentList(Long lectureGroupId, Pageable pageable){
        // delete yn
        Page<LectureAssignment> lectureAssignments = lectureAssignmentRepository.findByLectureGroupIdAndDelYn(lectureGroupId,"N",pageable);
        return lectureAssignments.map(LectureAssignmentListResponse::fromEntity);
    }

    public LectureAssignmentDetailResponse assignmentDetail(Long lectureAssignmentId){
        LectureAssignment lectureAssignment = lectureAssignmentRepository.findById(lectureAssignmentId).orElseThrow(()->new EntityNotFoundException("해당 과제가 없습니다."));
        return LectureAssignmentDetailResponse.fromEntity(lectureAssignment);
    }

    // 과제 수정
    public LectureAssignmentUpdateResponse assignmentUpdate(Long lectureAssignmentId, LectureAssignmentUpdateRequest lectureAssignmentUpdateRequest){
        // lecturegroup에서 lecture 정보 -> 현재 로그인 회원이 tutor인지 체크
        LectureAssignment lectureAssignment = lectureAssignmentRepository.findById(lectureAssignmentId).orElseThrow(()-> new EntityNotFoundException("해당 과제가 없습니다."));
        Long lectureGroupId = lectureAssignment.getLectureGroup().getId();
        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId).orElseThrow(()->new EntityNotFoundException("해당 그룹이 없습니다."));
        Lecture lecture = lectureGroup.getLecture();
        Long lectureTutorId = lecture.getMemberId();
        Long loginMemberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!lectureTutorId.equals(loginMemberId)) throw new RuntimeException("튜터만 과제를 수정할 수 있습니다.");

        // 과제 수정
        lectureAssignment.updateAssignment(lectureAssignmentUpdateRequest);
        LectureAssignment saveAssignment = lectureAssignmentRepository.save(lectureAssignment);

        List<LectureApply> lectureApplies = lectureApplyRepository.findMemberIdsByLectureGroupIdAndStatusAdmit(lectureGroupId);
        List<Long> tuteeList = new ArrayList<>();
        for(LectureApply lectureApply : lectureApplies){
            tuteeList.add(lectureApply.getMemberId());
        }
        // 튜터 : 강의 그룹 - 강의 id - 강의의 member id
        Long tutorId = lecture.getMemberId();
        // feign으로 강의 그룹에 해당되어 있는 튜터 튜티 모두에게 주어야함
        AssignmentUpdateRequest dto = AssignmentUpdateRequest.fromEntity(lectureGroupId,tutorId,tuteeList,saveAssignment);
        try {
            String message = objectMapper.writeValueAsString(dto);

            kafkaTemplate.send("assignment-update", message);  // JSON 문자열 전송

            System.out.println("Kafka 메시지 전송됨: " + message);
        } catch (JsonProcessingException e) {
            System.err.println("Kafka 메시지 변환 및 전송 실패: " + e.getMessage());
        }
        // 과제 결과값 리턴
        return LectureAssignmentUpdateResponse.fromEntity(lectureAssignment);

    }

    public String assignmentDelete(Long lectureAssignmentId){
        // lecturegroup에서 lecture 정보 -> 현재 로그인 회원이 tutor인지 체크
        LectureAssignment lectureAssignment = lectureAssignmentRepository.findById(lectureAssignmentId).orElseThrow(()-> new EntityNotFoundException("해당 과제가 없습니다."));
        Long lectureGroupId = lectureAssignment.getLectureGroup().getId();
        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId).orElseThrow(()->new EntityNotFoundException("해당 그룹이 없습니다."));
        Lecture lecture = lectureGroup.getLecture();
        Long lectureTutorId = lecture.getMemberId();
        Long loginMemberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        if(!lectureTutorId.equals(loginMemberId)) throw new RuntimeException("튜터만 과제를 삭제할 수 있습니다.");

        // 과제 수정
        lectureAssignment.updateDelYn();
        LectureAssignment saveAssignment = lectureAssignmentRepository.save(lectureAssignment);

        AssignmentDeleteRequest dto = AssignmentDeleteRequest.fromEntity(saveAssignment);
        try {
            String message = objectMapper.writeValueAsString(dto);

            kafkaTemplate.send("assignment-delete", message);  // JSON 문자열 전송

            System.out.println("Kafka 메시지 전송됨: " + message);
        } catch (JsonProcessingException e) {
            System.err.println("Kafka 메시지 변환 및 전송 실패: " + e.getMessage());
        }
        // 과제 결과값 리턴
        return saveAssignment.getId()+" : "+saveAssignment.getTitle()+"이 삭제되었습니다.";
    }
}
