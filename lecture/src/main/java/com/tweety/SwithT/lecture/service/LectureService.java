package com.tweety.SwithT.lecture.service;

import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.dto.*;
import com.tweety.SwithT.lecture.repository.GroupTimeRepository;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture.repository.LectureRepository;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import com.tweety.SwithT.lecture.domain.GroupTime;
import com.tweety.SwithT.lecture.domain.Lecture;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.dto.LectureDetailResDto;
import com.tweety.SwithT.lecture.dto.LectureGroupListResDto;
import com.tweety.SwithT.lecture.dto.LectureListResDto;
import com.tweety.SwithT.lecture.dto.LectureSearchDto;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LectureService {
    private final LectureRepository lectureRepository;
    private final LectureGroupRepository lectureGroupRepository;
    private final GroupTimeRepository groupTimeRepository;
    private final LectureApplyRepository lectureApplyRepository;

    // Create
    @Transactional
    public Lecture lectureCreate(LectureCreateReqDto lectureCreateReqDto, List<LectureGroupReqDto> lectureGroupReqDtos){
        Long memberId = Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName());

        // Lecture 정보 저장
        Lecture createdLecture = lectureRepository.save(lectureCreateReqDto.toEntity(memberId));

        for (LectureGroupReqDto groupDto : lectureGroupReqDtos){
            // Lecture Group 정보 저장
            LectureGroup createdGroup = lectureGroupRepository.save(groupDto.toEntity(createdLecture));
            for (GroupTimeReqDto timeDto : groupDto.getGroupTimeReqDtos()){
                groupTimeRepository.save(timeDto.toEntity(createdGroup));
            }
        }

        return createdLecture;
    }

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

    // 강의 수정
    @Transactional
    public LectureDetailResDto lectureUpdate(Long id, LectureUpdateReqDto dto){
        Lecture lecture = lectureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Lecture is not found"));

        if (dto.getTitle() != null) {
            lecture.updateTitle(dto.getTitle());
        }
        if (dto.getContents() != null) {
            lecture.updateContents(dto.getContents());
        }
        if (dto.getImage() != null) {
            lecture.updateImage(dto.getImage());
        }
        if (dto.getCategory() != null) {
            lecture.updateCategory(dto.getCategory());
        }
        System.out.println(lecture.fromEntityToLectureDetailResDto());
        return lecture.fromEntityToLectureDetailResDto();
    }

    // 강의 삭제
    @Transactional
    public void lectureDelete(Long lectureId) {
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(() -> new EntityNotFoundException("Lecture is not found"));

        List<LectureGroup> lectureGroups = lectureGroupRepository.findByLectureId(lectureId);

        for (LectureGroup group : lectureGroups) {
            List<LectureApply> lectureApplies = group.getLectureApplies();

            // LectureApply가 하나라도 존재한다면 삭제 불가
            if (!lectureApplies.isEmpty()) {
                throw new IllegalArgumentException("LectureApply가 존재하여 Lecture를 삭제할 수 없습니다.");
            }
        }

        // 모든 LectureGroup의 LectureApply가 없을 경우, Lecture와 각각의 LectureGroup 삭제
        lecture.updateDelYn();
        for (LectureGroup group : lectureGroups) {
            group.updateDelYn();
            // LectureGroup의 GroupTime 삭제
            for (GroupTime groupTime : group.getGroupTimes()){
                groupTime.updateDelYn();
            }
        }
    }

    // 강의 그룹 수정
    @Transactional
    public void lectureGroupUpdate(Long lectureGroupId, LectureGroupReqDto dto){
        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId)
                .orElseThrow(()->new EntityNotFoundException("Lecture group is not found"));

        // LectureApply가 하나라도 존재한다면 수정 불가
        if (!lectureGroup.getLectureApplies().isEmpty()) {
            throw new IllegalArgumentException("LectureApply가 존재하여 Lecture를 삭제할 수 없습니다.");
        }
        if (dto.getPrice() != null) {
            lectureGroup.updatePrice(dto.getPrice());
        }
        if (dto.getLimitPeople() != null) {
            lectureGroup.updateLimitPeople(dto.getLimitPeople());
        }
        if (dto.getLatitude() != null && dto.getLongitude() != null) {
            lectureGroup.updatePoint(dto.getLatitude(), dto.getLongitude());
        }
        if (dto.getStartDate() != null && dto.getEndDate() != null) {
            lectureGroup.updateDate(dto.getStartDate(), dto.getEndDate());
        }
        if (dto.getGroupTimeReqDtos() != null){
            for (GroupTime groupTime : groupTimeRepository.findByLectureGroupId(lectureGroupId)){
                groupTime.updateDelYn();
            }
            for (GroupTimeReqDto timeDto : dto.getGroupTimeReqDtos()){
                groupTimeRepository.save(timeDto.toEntity(lectureGroup));
            }
        }

    }

    // 강의 그룹 삭제
    @Transactional
    public void lectureGroupDelete(Long lectureGroupId){
        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId)
                .orElseThrow(()->new EntityNotFoundException("Lecture group is not found"));

        // LectureApply가 하나라도 존재한다면 삭제 불가
        if (!lectureGroup.getLectureApplies().isEmpty()) {
            throw new IllegalArgumentException("LectureApply가 존재하여 Lecture를 삭제할 수 없습니다.");
        }
        lectureGroup.updateDelYn();
        for (GroupTime groupTime : lectureGroup.getGroupTimes()){
            groupTime.updateDelYn();
        }
    }

}
