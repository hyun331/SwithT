package com.tweety.SwithT.lecture_apply.service;

import com.tweety.SwithT.common.service.RedisStreamProducer;
import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
import com.tweety.SwithT.lecture_apply.domain.GroupLimit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class WaitingService {

    @Qualifier("5")
    private final RedisTemplate<String, Object> redisTemplate;

    private final LectureGroupRepository lectureGroupRepository;

    private static final long FIRST_ELEMENT = 0;
    private static final long LAST_ELEMENT = -1;
    private static final long PUBLISH_SIZE = 10;
    private static final long LAST_INDEX = 1;

    private GroupLimit groupLimit;

//    @Getter
//    private GroupLimit groupLimit;

    private final RedisStreamProducer redisStreamProducer;

    private final ConcurrentHashMap<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 그룹 제한 인원 수 지정
    public void setGroupLimit(Long lectureGroupId, int limitPeople){
        this.groupLimit = new GroupLimit(lectureGroupId, limitPeople);
    }

    public String addQueue(Long lectureGroupId, Long memberId){
        // 대기열 안에 회원이 없는 경우, 대기열에 추가
        Double enterTime = redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId);
        if (enterTime == null) {
            final long now = System.currentTimeMillis();
            redisTemplate.opsForZSet().add(lectureGroupId.toString(), memberId, (int) now);
            log.info("대기열에 추가 - {} ({}초)", memberId, now);
            System.out.println("Successfully created & applied for the lecture.");
            return "Successfully created & applied for the lecture.";
        } else {
            // 대기열 안에 회원이 있는 경우
            System.out.println("이미 대기열에 진입한 유저입니다.");
            return "User is already in the queue.";
        }
    }


    public void getOrder(String memberId, String queueKey) throws InterruptedException {

        Set<Object> queue = redisTemplate.opsForZSet().range(queueKey, 0, -1);

        for (Object people : queue) {
            Long rank = redisTemplate.opsForZSet().rank(queueKey, people);
            log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
//            redisStreamProducer.publishWaitingMessage(memberId, "WAITING", "대기열 조회", rank.toString());
            redisStreamProducer.publishWaitingMessage(memberId, "WAITING", queueKey+"번 강의 대기열 조회", rank.toString());
        }

        Thread.sleep(1000);


//        SseEmitter emitter = new SseEmitter();
//        emitters.put(memberId, emitter);

//        // 1초마다 대기열 순위 전송
//        new Thread(() -> {
//            try {
//                while (true) {
//                    System.out.println("대기열 조회 시작!!!!!!!!!"); // 확인
//                    // *** 여기 안탐
//                    Long position = redisTemplate.opsForZSet().rank(queueKey, memberId);    // 순번 조회
//                    System.out.println("position: " +position);  // *** null
//                    if (position != null) {
//                        redisStreamProducer.publishMessage(memberId, "WAITING", "대기열 조회", position.toString());
//                        System.out.println("Your position in the queue:" + (position + 1)); // *** 실패
//                    } else {
//                        break;
//                    }
//                    Thread.sleep(1000);
//                }
//            } catch (InterruptedException e) {
//                emitter.completeWithError(e);
//            } finally {
//                emitters.remove(memberId);
//            }
//        }).start();
    }
    // 결제 처리
    public void processPayment(Long lectureGroupId) {
        if (validEnd()) {
            log.info("대기열이 종료되었습니다. 결제 처리 불가능.");
            return;
        }

        final long start = FIRST_ELEMENT;
        final long end = 10; // 제한 인원만큼 결제 처리
        log.info("결제 전 현재 남은 자리수: {}", groupLimit.getLimit());

        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
        for (Object people : queue) {
            // 결제 페이지로 넘기기
            log.info("'{}'님에 대한 결제가 완료되었습니다.", people);
            System.out.println(people + "님의 강의 그룹 결제가 완료되었습니다.");
            redisTemplate.opsForZSet().remove(lectureGroupId.toString(), people);
            this.groupLimit.decrease();
        }

        // 결제 후 대기열 상태 로깅
        log.info("결제 후 현재 남은 자리수: {}", groupLimit.getLimit());

        // 그룹 리밋이 0이 되었는지 체크
        if (groupLimit.getLimit() <= 0) {
            log.info("대기열이 종료되었습니다.");
        }
    }
    //
    public boolean validEnd(){
        return this.groupLimit != null
                ? this.groupLimit.end()
                : false;
    }

    public long getSize(Long lectureGroupId) {
        return redisTemplate.opsForZSet().size(lectureGroupId.toString());
    }




//    // 결제 처리
//    public void processPayment(Long lectureGroupId) {
//
//        final long start = FIRST_ELEMENT;
//        final long end = PUBLISH_SIZE - LAST_INDEX;
//
//        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
//        for (Object people : queue) {
//
//            // 결제 api 추가
//            // 결제 차례임을 알려주는 알림
//            log.info("'{}'님에 대한 결제가 완료되었습니다.", people);
//            redisTemplate.opsForZSet().remove(lectureGroupId.toString(), people);
//            this.groupLimit.decrease();
//        }

//        // 결제 후 대기열 상태 로깅
//        log.info("결제 후 현재 대기열 인원: {}", groupLimit.getLimit());
//
//        // 그룹 리밋이 0이 되었는지 체크
//        if (groupLimit.getLimit() <= 0) {
//            log.info("대기열이 종료되었습니다.");
//        }
    }


//    @GetMapping("/events/{userId}")
//    public SseEmitter handleSse(@PathVariable String userId) {
//        SseEmitter emitter = emitters.get(userId);
//        if (emitter == null) {
//            emitter = new SseEmitter();
//            emitters.put(userId, emitter);
//        }
//        return emitters.get(userId);
//    }





//    // 강의 그룹 신청
//    public void createQueue(Long lectureGroupId, int limitPeople) {
//        this.groupLimit = new GroupLimit(lectureGroupId, limitPeople);
////        LectureGroup lectureGroup = lectureGroupRepository.findById(lectureGroupId).orElseThrow(()->{
////                    throw new EntityNotFoundException("해당 id에 맞는 강의 그룹이 존재하지 않습니다.");
////        });
//
//        log.info("대기열 생성 - 강의 그룹 ID: {}, 제한 인원: {}", lectureGroupId, limitPeople);
//    }
//
//    // groupLimit의 lectureGroupId를 반환
//    public Long getLectureGroupId() {
//        return groupLimit != null ? groupLimit.getLectureGroupId() : null;
//    }
//
//    // 대기열에 추가
//    public void addQueue(Long lectureGroupId, Long memberId) {
//
//        if (validEnd()) {
//            log.warn("대기열이 종료되었습니다.");
//            return;
//        }
////        Long memberId = Long.parseLong(SecurityContextHolder.getContext().getAuthentication().getName());
//
////        final String people = Thread.currentThread().getName();
//        final String people = memberId.toString();
//        final long now = System.currentTimeMillis();
//
//        redisTemplate.opsForZSet().add(lectureGroupId.toString(), people, now);
//        log.info("대기열에 추가 - {} ({}초)", people, now);
//    }
//
//    // 대기열 상태 조회
//    public void getOrder(Long lectureGroupId) {
//        final long start = FIRST_ELEMENT;
//        final long end = LAST_ELEMENT;
//
////        // lectureGroupId를 key값으로 갖는 set의 start~end번을 조회
////        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
////        if (queue != null) {
////            for (Object people : queue) {
////                Long rank = redisTemplate.opsForZSet().rank(lectureGroupId.toString(), people);
////                System.out.println("rank:" + rank);
////                if (rank != null) { // rank가 null이 아닌 경우에만 출력
////
////                    log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
////
//////                    redisStreamProducer.publishMessage(people.toString(), "대기열 조회", "대기열 조회", rank.toString());
////
////                } else {
////                    log.warn("'{}'님의 순위를 가져오는 데 실패했습니다.", people);
////                }
////            }
////        } else {
////            log.info("대기열에 등록된 사람이 없습니다.");
////        }
//        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
//        if (queue != null) {
//            for (Object obj : queue) {
//                // obj가 String 타입으로 저장되었으므로 Long으로 변환
//                if (obj instanceof String memberIdString) {
//                    Long memberId = Long.valueOf(memberIdString); // String을 Long으로 변환
//                    Long rank = redisTemplate.opsForZSet().rank(lectureGroupId.toString(), memberIdString);
//                    if (rank != null) {
//                        log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", memberId, rank);
//                        redisStreamProducer.publishMessage(memberId.toString(), "대기열 조회", "대기열 조회", rank.toString());
//                    } else {
//                        log.warn("'{}'님의 순위를 가져오는 데 실패했습니다.", memberId);
//                    }
//                }
//            }
//        } else {
//            log.info("대기열에 등록된 사람이 없습니다.");
//        }
//    }
//


