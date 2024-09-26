package com.tweety.SwithT.scheduler.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import com.tweety.SwithT.scheduler.dto.GroupTimeResDto;
import com.tweety.SwithT.scheduler.dto.ScheduleCreateDto;
import com.tweety.SwithT.scheduler.repository.SchedulerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class SchedulerService {

    private final SchedulerRepository schedulerRepository;
    private final ObjectMapper objectMapper;
    private final MemberRepository memberRepository;

    public SchedulerService(SchedulerRepository schedulerRepository, ObjectMapper objectMapper, MemberRepository memberRepository) {
        this.schedulerRepository = schedulerRepository;
        this.objectMapper = objectMapper;
        this.memberRepository = memberRepository;
    }

    @KafkaListener(topics = "schedule-update", groupId = "member-group", containerFactory = "kafkaListenerContainerFactory")
    public void updateScheduleFromKafka(String message) {
        try {
            System.out.println("수신된 Kafka 메시지: " + message);

//            아래 코드 없으면 "{\"lectureId\":1,\"status\":\"ADMIT\"}" 이중 직렬화 되어있어 계속 에러 발생
            if (message.startsWith("\"") && message.endsWith("\"")) {
                // 이스케이프 문자와 이중 직렬화를 제거
                message = message.substring(1, message.length() - 1).replace("\\", "");
                System.out.println("이중 직렬화 제거 후 메시지: " + message);
            }
            List<GroupTimeResDto> groupTimeResDtos = objectMapper.readValue(
                    message,
                    new TypeReference<>() {
                    } // 리스트 형태로 변환
            );

            Member member = memberRepository.findById(groupTimeResDtos.get(0).getMemberId()).orElseThrow(
                    () -> new EntityNotFoundException("존재하지 않는 회원입니다."));

            // 각 GroupTimeResDto를 처리하는 로직
            for (GroupTimeResDto groupTimeResDto : groupTimeResDtos) {
                if (groupTimeResDto.getLectureType().equals("LECTURE")) {
                    LocalDate startDate = LocalDate.parse(groupTimeResDto.getStartDate());
                    LocalDate endDate = LocalDate.parse(groupTimeResDto.getEndDate());

                    // GroupTimeResDto에 있는 요일 정보 (MON, TUE 등)를 DayOfWeek로 변환
                    DayOfWeek lectureDay = DayOfWeek.valueOf(groupTimeResDto.getLectureDay().toUpperCase());

                    List<Scheduler> schedulers = new ArrayList<>();  // 스케줄러 리스트를 생성

                    // startDate부터 endDate까지 날짜를 반복하면서 요일을 확인
                    for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
                        if (date.getDayOfWeek().equals(lectureDay)) {
                            // 요일이 일치하면 스케줄러 생성
                            Scheduler scheduler = Scheduler.builder()
                                    .title(groupTimeResDto.getSchedulerTitle())
                                    .schedulerDate(date)
                                    .schedulerTime(LocalTime.parse(groupTimeResDto.getStartTime()))  // 강의 시작 시간 설정
                                    .content(groupTimeResDto.getSchedulerTitle() + "가 있는 날입니다.")
                                    .alertYn(groupTimeResDto.getAlertYn())
                                    .member(member)
                                    .lectureGroupId(groupTimeResDto.getLectureGroupId())
                                    .build();

                            schedulers.add(scheduler);
                        }
                    }
                    schedulerRepository.saveAll(schedulers);
                }
            }
        } catch (JsonProcessingException e) {
            System.err.println("Kafka 메시지 변환 중 오류 발생: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Lecture 상태 업데이트 중 오류 발생: " + e.getMessage());
        }
    }

    public Scheduler addSchedule(ScheduleCreateDto dto){

        Member member = memberRepository.findById(
                Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName())).orElseThrow(
                ()-> new EntityNotFoundException("존재하지 않는 회원 정보입니다."));

        Scheduler scheduler = dto.toEntity(member);
        schedulerRepository.save(scheduler);

        return scheduler;
    }

    public void deleteSchedule(Long schedulerId){
        Member member = memberRepository.findById(
                Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName())).orElseThrow(
                ()-> new EntityNotFoundException("존재하지 않는 회원 정보입니다."));

        Scheduler scheduler = schedulerRepository.findById(schedulerId).orElseThrow(
                ()-> new EntityNotFoundException("스케줄 정보 불러오기에 실패했습니다"));
        if(!scheduler.getMember().equals(member)){
            throw new IllegalArgumentException("접근 권한이 없습니다.");
        }
        scheduler.deleteSchedule();
        schedulerRepository.save(scheduler);
    }
}