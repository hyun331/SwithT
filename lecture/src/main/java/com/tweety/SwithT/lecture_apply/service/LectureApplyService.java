package com.tweety.SwithT.lecture_apply.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.common.dto.CommonResDto;
import com.tweety.SwithT.common.service.MemberFeign;
import com.tweety.SwithT.common.service.RedisStreamProducer;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.common.dto.MemberNameResDto;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture.repository.LectureRepository;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import com.tweety.SwithT.lecture_apply.dto.SingleLectureApplyAfterResDto;
import com.tweety.SwithT.lecture_apply.dto.SingleLectureApplyListDto;
import com.tweety.SwithT.lecture_apply.dto.SingleLectureApplySavedDto;
import com.tweety.SwithT.lecture_apply.repository.LectureApplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;


import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureApplyService {
    private final LectureGroupRepository lectureGroupRepository;
    private final LectureApplyRepository lectureApplyRepository;
    private final LectureRepository lectureRepository;
    private final MemberFeign memberFeign;
    private final RedisStreamProducer redisStreamProducer;



    @Value("${jwt.secretKey}")
    private String secretKey;

    //튜티가 과외 신청
    @Transactional
    public SingleLectureApplyAfterResDto tuteeSingleLectureApply(SingleLectureApplySavedDto dto) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        CommonResDto commonResDto = memberFeign.getMemberNameById(memberId);
        ObjectMapper objectMapper = new ObjectMapper();
        MemberNameResDto memberNameResDto = objectMapper.convertValue(commonResDto.getResult(), MemberNameResDto.class);
        String memberName = memberNameResDto.getName();


        LectureGroup lectureGroup = lectureGroupRepository.findById(dto.getLectureGroupId()).orElseThrow(()->{
            throw new EntityNotFoundException("해당 과외는 존재하지 않습니다.");
        });
        if(lectureGroup.getIsAvailable().equals("N")){
            throw new RuntimeException("해당 과외는 신청할 수 없습니다.");
        }

        List<LectureApply> lectureApplyList = lectureApplyRepository.findByMemberIdAndLectureGroup(memberId, lectureGroup);
        if(!lectureApplyList.isEmpty()){
            int rejectedCount = 0;
            for(LectureApply lectureApply : lectureApplyList){
                if(lectureApply.getStatus() == Status.STANDBY){
                    throw new RuntimeException("이미 신청한 과외입니다.");
                }
                if(lectureApply.getStatus() == Status.REJECT){
                    rejectedCount++;
                    if(rejectedCount>=3){
                        throw new RuntimeException("해당 과외는 3회 이상 거절되어 신청할 수 없습니다.");
                    }
                }
            }
        }
        lectureApplyRepository.save(dto.toEntity(lectureGroup, memberId, memberName));

        Lecture lecture = lectureRepository.findById(lectureGroup.getLecture().getId()).orElseThrow(()->{
            throw new EntityNotFoundException("해당 과외가 존재하지 않습니다.");
        });

        return SingleLectureApplyAfterResDto.builder().lectureTitle(lecture.getTitle()).build();


    }

    //튜터가 보는 강의그룹 신청자 리스트
    public Page<SingleLectureApplyListDto> singleLectureApplyList(Long id, Pageable pageable) {
        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());

        LectureGroup lectureGroup = lectureGroupRepository.findById(id).orElseThrow(()->{
            throw new EntityNotFoundException("해당 강의 그룹이 없습니다");
        });
        Lecture lecture = lectureGroup.getLecture();
        if(lecture.getMemberId() != memberId){  //소유자가 아닌 경우
            throw new IllegalArgumentException("접근할 수 없는 강의 그룹입니다");
        }
        Page<LectureApply> lectureApplies = lectureApplyRepository.findByLectureGroup(lectureGroup, pageable);
        lectureApplies.stream().filter(a-> a.getStatus()==Status.WAITING || a.getStatus()==Status.STANDBY);
        return lectureApplies.map(a->a.fromEntityToSingleLectureApplyListDto());

    }

    @Transactional
    public String singleLecturePaymentRequest(Long id) {
        LectureApply lectureApply = lectureApplyRepository.findById(id).orElseThrow(()->{
            throw new EntityNotFoundException("id에 맞는 수강을 찾을 수 없습니다.");
        });
        LectureGroup lectureGroup = lectureApply.getLectureGroup();

        if(lectureApplyRepository.findByLectureGroupAndStatus(lectureGroup, Status.WAITING).isPresent()){
            throw new IllegalArgumentException("결제 대기 중인 튜티가 존재합니다.");
        }

        lectureApply.updateStatus(Status.WAITING);

        //결제 요청 보내기
        redisStreamProducer.publishMessage(lectureApply.getMemberId().toString(), "결제요청", "수학천재가 되는 길에서 결제 요청을 했습니다.", lectureApply.getId().toString());


        return "ok ok";
    }
}
