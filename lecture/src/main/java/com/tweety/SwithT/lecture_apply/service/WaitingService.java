package com.tweety.SwithT.lecture_apply.service;

import com.tweety.SwithT.common.domain.Status;
import com.tweety.SwithT.common.service.RedisStreamProducer;
import com.tweety.SwithT.lecture.domain.LectureGroup;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture_apply.domain.LectureApply;
import com.tweety.SwithT.lecture_apply.repository.LectureApplyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

    @Qualifier("5")
    private final RedisTemplate<String, Object> redisTemplate;

    private final LectureGroupRepository lectureGroupRepository;

    private final LectureApplyRepository lectureApplyRepository;

    private final RedisStreamProducer redisStreamProducer;


//    public String addQueue(Long lectureGroupId, Long memberId) {
//
//        // 대기열 안에 회원이 없는 경우, 대기열에 추가
//        Double enterTime = redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId);
//        if (enterTime == null) {
//            final long now = System.currentTimeMillis();
//            redisTemplate.opsForZSet().add(lectureGroupId.toString(), memberId, (int) now);
//            log.info("대기열에 추가 - {}번 유저 ({}초)", memberId, now);
//            System.out.println("Successfully created & applied for the lecture.");
//            return "Successfully created & applied for the lecture.";
//        } else {
//            // 대기열 안에 회원이 있는 경우
//            log.info("이미 대기열에 진입한 유저입니다. {}번 유저 ", memberId);
//            return "User is already in the queue.";
//        }
//    }
//
//
    public void getOrder(String memberId, String queueKey){

        Set<Object> queue = redisTemplate.opsForZSet().range(queueKey, 0, -1);

        for (Object people : queue) {
            Long rank = redisTemplate.opsForZSet().rank(queueKey, people);
            log.info("'{}'번 유저의 현재 대기열은 {}명 남았습니다.", people, rank);
            redisStreamProducer.publishWaitingMessage(memberId, "WAITING", queueKey + "번 강의 대기열 조회", rank.toString());
        }

//        Thread.sleep(1000);
    }
//
//
//    // 결제 처리
//    public void processPayment(LectureGroup lectureGroup) {
//
//        String queueKey = lectureGroup.getId().toString();
//
//        if (lectureGroup.end() || lectureGroup.getRemaining() <= 0) {
//            log.info("대기열이 종료되었습니다. 결제 처리 불가능.");
//            return;
//        }
//
//        final long start = 0;
//        final long end = 1; // 제한 인원만큼 결제 처리
//
//        log.info("결제 전 현재 남은 자리수: {}", lectureGroup.getRemaining());
//
//        Set<Object> queue = redisTemplate.opsForZSet().range(queueKey, start, end);
//        for (Object people : queue) {
//
//            // 결제 페이지로 넘기기
//            log.info("'{}'님에 대한 결제가 완료되었습니다.", people);
//            System.out.println(people + "번 유저의 강의 그룹 결제가 완료되었습니다.");
//
//            // 큐에서 제거  ** 잘 안되고 있는거 같음
//            redisTemplate.opsForZSet().remove(queueKey, people);
//
//            // 남은 자리수 감소  ** 잘 안되고 있는거 같음
//            lectureGroup.decreaseRemaining();
//
//            // 결제 후 대기열 상태 로깅
//            log.info("결제 후 현재 남은 자리수: {}", lectureGroup.getRemaining());
//
//            // 그룹 리밋이 0이 되었는지 체크
//            if (lectureGroup.getRemaining() <= 0) {
//                log.info("대기열이 종료되었습니다.");
//            }
//        }
////        lectureGroupRepository.save(lectureGroup);
//
//    }

    public String addQueue(Long lectureGroupId, Long memberId) {
        // 대기열 안에 회원이 없는 경우, 대기열에 추가
        Double enterTime = redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId);
        if (enterTime == null) {
            final long now = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(lectureGroupId.toString(), memberId, (int) now);
            log.info("대기열에 추가 - {}번 유저 ({}초)", memberId, now);

            // 대기열 상태 확인
            checkAndUpdateStatus(lectureGroupId);

            return "Successfully created & applied for the lecture.";
        } else {
            // 대기열 안에 회원이 있는 경우
            log.info("이미 대기열에 진입한 유저입니다. {}번 유저 ", memberId);
            return "User is already in the queue.";
        }
    }

    private void checkAndUpdateStatus(Long lectureGroupId) {
        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), 0, 0); // 앞에 있는 사람

        if (queue != null && !queue.isEmpty()) {
            // 앞에 있는 유저가 결제 처리되었다고 가정
            Object frontUser = queue.iterator().next();

            // 큐에서 제거
            redisTemplate.opsForZSet().remove(lectureGroupId.toString(), frontUser);

            // 강의 그룹 상태 업데이트
            LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 강의는 존재하지 않습니다."));

            // 남은 자리수 감소
            lectureGroup.decreaseRemaining();

            // 대기열 상태 저장
            if (lectureGroup.getRemaining() > 0) {
                lectureApplyRepository.save(LectureApply.builder()
                        .lectureGroup(lectureGroup)
                        .memberId((Long) frontUser) // 여기서 실제 유저 ID를 넣어야 함
                        .status(Status.STANDBY)
                        .build());
            }

            log.info("현재 대기열 상태 업데이트 완료.");
        }
    }


    public long getQueueSize(Long lectureGroupId) {
        return redisTemplate.opsForZSet().size(lectureGroupId.toString());
    }
}



