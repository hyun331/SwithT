package com.tweety.SwithT.lecture_apply.service;

import com.tweety.SwithT.lecture_apply.dto.GroupLimit;
import lombok.Getter;
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

    private static final long FIRST_ELEMENT = 0;
    private static final long LAST_ELEMENT = -1;
    @Getter
    private GroupLimit groupLimit;

    // 강의 그룹 신청
    public void createQueue(Long lectureGroupId, int limitPeople) {
        this.groupLimit = new GroupLimit(lectureGroupId, limitPeople);
        log.info("대기열 생성 - 강의 그룹 ID: {}, 제한 인원: {}", lectureGroupId, limitPeople);
    }

    // groupLimit의 lectureGroupId를 반환
    public Long getLectureGroupId() {
        return groupLimit != null ? groupLimit.getLectureGroupId() : null;
    }

    // 대기열에 추가
    public void addQueue(Long lectureGroupId) {
        if (validEnd()) {
            log.warn("대기열이 종료되었습니다.");
            return;
        }

        final String people = Thread.currentThread().getName();
        final long now = System.currentTimeMillis();

        redisTemplate.opsForZSet().add(lectureGroupId.toString(), people, now);
        log.info("대기열에 추가 - {} ({}초)", people, now);
    }

    // 대기열 상태 조회
    public void getOrder(Long lectureGroupId) {
        final long start = FIRST_ELEMENT;
        final long end = LAST_ELEMENT;

        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
        if (queue != null) {
            for (Object people : queue) {
                Long rank = redisTemplate.opsForZSet().rank(lectureGroupId.toString(), people);
                if (rank != null) { // rank가 null이 아닌 경우에만 출력
                    log.info("'{}'님의 현재 대기열은 {}명 남았습니다.", people, rank);
                } else {
                    log.warn("'{}'님의 순위를 가져오는 데 실패했습니다.", people);
                }
            }
        } else {
            log.info("대기열에 등록된 사람이 없습니다.");
        }
    }

    // 결제 처리
    public void processPayment(Long lectureGroupId) {
        if (validEnd()) {
            log.info("대기열이 종료되었습니다. 결제 처리 불가능.");
            return;
        }

        final long start = FIRST_ELEMENT;
        final long end = groupLimit.getLimitPeople() - 1; // 제한 인원만큼 결제 처리
        log.info("결제 전 현재 대기열 인원: {}", groupLimit.getLimitPeople());

        Set<Object> queue = redisTemplate.opsForZSet().range(lectureGroupId.toString(), start, end);
        for (Object people : queue) {
            // 결제 로직 구현
            log.info("'{}'님에 대한 결제가 완료되었습니다.", people);
            redisTemplate.opsForZSet().remove(lectureGroupId.toString(), people);
            this.groupLimit.decrease();
        }

        // 결제 후 대기열 상태 로깅
        log.info("결제 후 현재 대기열 인원: {}", groupLimit.getLimitPeople());

        // 그룹 리밋이 0이 되었는지 체크
        if (groupLimit.getLimitPeople() <= 0) {
            log.info("대기열이 종료되었습니다.");
        }
    }

    public boolean validEnd(){
        return this.groupLimit != null && this.groupLimit.end();
    }

    public long getSize(Long lectureGroupId) {
        return redisTemplate.opsForZSet().size(lectureGroupId.toString());
    }

}
