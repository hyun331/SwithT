package com.tweety.SwithT.lecture.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.lecture.domain.GroupTime;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.dto.*;
import com.tweety.SwithT.lecture.repository.GroupTimeRepository;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture.repository.LectureRepository;
import com.tweety.SwithT.lecture_apply.repository.LectureApplyRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureGroupRepository lectureGroupRepository;
    private final GroupTimeRepository groupTimeRepository;
    private final LectureApplyRepository lectureApplyRepository;
    private final ObjectMapper objectMapper;

    public LectureService(LectureRepository lectureRepository, LectureGroupRepository lectureGroupRepository, GroupTimeRepository groupTimeRepository, LectureApplyRepository lectureApplyRepository, ObjectMapper objectMapper){

        this.lectureRepository = lectureRepository;
        this.lectureGroupRepository = lectureGroupRepository;
        this.groupTimeRepository = groupTimeRepository;
        this.lectureApplyRepository = lectureApplyRepository;
        this.objectMapper = objectMapper;
    }
    // Create
    @Transactional
    public Lecture lectureCreate(LectureCreateReqDto lectureCreateReqDto, List<LectureGroupReqDto> lectureGroupReqDtos){
        Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        // Lecture 정보 저장
        Lecture createdLecture = lectureRepository.save(lectureCreateReqDto.toEntity(memberId));

        for (LectureGroupReqDto groupDto : lectureGroupReqDtos){
            // Lecture Group 정보 저장
            LectureGroup createdGroup = lectureGroupRepository.save(groupDto.toEntity(createdLecture));
            System.out.println(createdGroup.getId());
            for (GroupTimeReqDto timeDto : groupDto.getGroupTimeReqDtos()){
                System.out.println(timeDto.getEndTime());
                System.out.println(timeDto.getStartTime());
                groupTimeRepository.save(timeDto.toEntity(createdGroup));
            }
        }

        return createdLecture;
    }


    // Update: limitPeople=0
//    public void lectureUpdate(LectureUpdateReqDto lectureUpdateReqDto, List<LectureGroupReqDto> lectureGroupReqDtos){
//        Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());
//        if (memberId == )
//    }

    // Delete: role=TUTOR & limitPeople=0



    public Page<LectureListResDto> showLectureList(LectureSearchDto searchDto, Pageable pageable) {
        Specification<Lecture> specification = new Specification<Lecture>() {
            @Override
            public Predicate toPredicate(Root<Lecture> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();
                if(searchDto.getSearchTitle() != null){
                    predicates.add(criteriaBuilder.like(root.get("title"), "%"+searchDto.getSearchTitle()+"%"));
                }
                if(searchDto.getCategory() != null){
                    predicates.add(criteriaBuilder.like(root.get("category"), "%"+searchDto.getCategory()+"%"));
                }
                if(searchDto.getLectureType() != null){
                    predicates.add(criteriaBuilder.like(root.get("lectureType"), "%"+searchDto.getLectureType()+"%"));
                }
                if(searchDto.getStatus() != null){
                    predicates.add(criteriaBuilder.like(root.get("status"), "%"+searchDto.getStatus()+"%"));
                }


                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicateArr.length; i++){
                    predicateArr[i] = predicates.get(i);
                }
                return criteriaBuilder.and(predicateArr);
            }
        };
        Page<Lecture> lectures = lectureRepository.findAll(specification, pageable);

        return lectures.map(Lecture::fromEntityToLectureListResDto);
    }


    public Page<LectureListResDto> showMyLectureList(LectureSearchDto searchDto, Pageable pageable) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Specification<Lecture> specification = new Specification<Lecture>() {
            @Override
            public Predicate toPredicate(Root<Lecture> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("memberId"), memberId));

                if(searchDto.getSearchTitle() != null){
                    predicates.add(criteriaBuilder.like(root.get("title"), "%"+searchDto.getSearchTitle()+"%"));
                }
                if(searchDto.getCategory() != null){
                    predicates.add(criteriaBuilder.like(root.get("category"), "%" + searchDto.getCategory() + "%"));
                }
                if (searchDto.getLectureType() != null) {
                    predicates.add(criteriaBuilder.like(root.get("lectureType"), "%" + searchDto.getLectureType() + "%"));
                }
                if (searchDto.getStatus() != null) {
                    predicates.add(criteriaBuilder.like(root.get("status"), "%" + searchDto.getStatus() + "%"));
                }


                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicateArr.length; i++){
                    predicateArr[i] = predicates.get(i);
                }
                return criteriaBuilder.and(predicateArr);
            }
        };
        Page<Lecture> lectures = lectureRepository.findAll(specification, pageable);

        return lectures.map(Lecture::fromEntityToLectureListResDto);
    }

    //강의 상세 화면
    public LectureDetailResDto lectureDetail(Long id) {
        Lecture lecture = lectureRepository.findById(id).orElseThrow(()->{
            throw new EntityNotFoundException("해당 id에 맞는 강의가 존재하지 않습니다.");
        });
        return lecture.fromEntityToLectureDetailResDto();
    }


    public Page<LectureGroupListResDto> showLectureGroupList(Long id, String isAvailable, Pageable pageable) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
        Lecture lecture = lectureRepository.findById(id).orElseThrow(()->{
           throw new EntityNotFoundException("해당 id에 맞는 강의/과외가 존재하지 않습니다.");
        });
        if(lecture.getMemberId() != memberId){
            throw new IllegalArgumentException("로그인한 유저는 해당 과외의 튜터가 아닙니다.");
        }

        Specification<LectureGroup> specification = new Specification<LectureGroup>() {
            @Override
            public Predicate toPredicate(Root<LectureGroup> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(criteriaBuilder.equal(root.get("lecture"), lecture));

                if(isAvailable != null && !isAvailable.isEmpty()){
                    predicates.add(criteriaBuilder.equal(root.get("isAvailable"), isAvailable));
                }
                Predicate[] predicateArr = new Predicate[predicates.size()];
                for(int i=0; i<predicateArr.length; i++){
                    predicateArr[i] = predicates.get(i);
                }
                return criteriaBuilder.and(predicateArr);
            }
        };
        Page<LectureGroup> lectureGroups = lectureGroupRepository.findAll(specification, pageable);
        Page<LectureGroupListResDto> lectureGroupResDtos = lectureGroups.map((a)->{
            List<GroupTime> groupTimeList = groupTimeRepository.findByLectureGroupId(a.getId());
            StringBuilder groupTitle = new StringBuilder();
            for(GroupTime groupTime : groupTimeList){
                groupTitle.append(groupTime.getLectureDay()+" "+groupTime.getStartTime()+"-"+groupTime.getEndTime()+"  /  ");
            }

            if (groupTitle.length() > 0) {
                groupTitle.setLength(groupTitle.length() - 5);
            }

            return LectureGroupListResDto.builder()
                    .title(groupTitle.toString())
                    .lectureGroupId(a.getId())
                    .build();
        });

        return lectureGroupResDtos;
    }

//    @KafkaListener(topics = "lecture-status-update", groupId = "lecture-group",
//            containerFactory = "kafkaListenerContainerFactory")
//    public void lectureStatusUpdate(String message) throws JsonProcessingException {
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        LectureStatusUpdateDto statusUpdateDto = objectMapper.convertValue(
//                message, LectureStatusUpdateDto.class);
//        Lecture lecture = lectureRepository.findById(statusUpdateDto.getLectureId()).orElseThrow(
//                ()-> new EntityNotFoundException("강의 정보 가져오기에 실패했습니다."));
//        lecture.updateStatus(statusUpdateDto);
//
//    }

    @Transactional
    public void updateLectureStatus(LectureStatusUpdateDto statusUpdateDto) {
        Lecture lecture = lectureRepository.findById(statusUpdateDto.getLectureId())
                .orElseThrow(() -> new EntityNotFoundException("강의 정보 가져오기에 실패했습니다."));

        // String으로 받은 status를 다시 Enum으로 변환
        Status newStatus = Status.valueOf(statusUpdateDto.getStatus().toUpperCase());

        // 강의 상태를 업데이트하고 저장
        lecture.updateStatus(newStatus);
        lectureRepository.save(lecture);
    }

    @KafkaListener(topics = "lecture-status-update", groupId = "lecture-group", containerFactory = "kafkaListenerContainerFactory")
    public void lectureStatusUpdateFromKafka(String message) {
        try {
//            System.out.println("수신된 Kafka 메시지: " + message);

//            아래 코드 없으면 "{\"lectureId\":1,\"status\":\"ADMIT\"}" 이중 직렬화 되어있어 계속 에러 발생
            if (message.startsWith("\"") && message.endsWith("\"")) {
                // 이스케이프 문자와 이중 직렬화를 제거
                message = message.substring(1, message.length() - 1).replace("\\", "");
//                System.out.println("이중 직렬화 제거 후 메시지: " + message);
            }

            // JSON 메시지를 LectureStatusUpdateDto로 변환
            LectureStatusUpdateDto statusUpdateDto = objectMapper.readValue(message, LectureStatusUpdateDto.class);

            // 상태 업데이트
            updateLectureStatus(statusUpdateDto);
//            System.out.println("Kafka 메시지 처리 완료: " + statusUpdateDto);
        } catch (JsonProcessingException e) {
            System.err.println("Kafka 메시지 변환 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lecture 상태 업데이트 중 오류 발생: " + e.getMessage());
        }
    }
}
