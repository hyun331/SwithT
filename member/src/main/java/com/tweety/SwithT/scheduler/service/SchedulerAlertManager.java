package com.tweety.SwithT.scheduler.service;

import com.tweety.SwithT.common.configs.RabbitMqConfig;
import com.tweety.SwithT.scheduler.domain.ScheduleAlert;
import com.tweety.SwithT.scheduler.dto.ScheduleAlertDto;
import com.tweety.SwithT.scheduler.repository.SchedulerAlertRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Component
public class SchedulerAlertManager {

    private final SchedulerAlertRepository schedulerAlertRepository;
    private final RabbitTemplate rabbitTemplate;

    private static final String SCHEDULED_ALERTS_ZSET_KEY = "scheduled_alerts";
    private static final String LOCK_KEY_UPDATE_QUEUE = "schedulerLock:updateSchedulerQueue"; // updateSchedulerQueue용 락 키
    private static final String LOCK_KEY_PROCESS_ALERTS = "schedulerLock:processScheduledAlerts"; // processScheduledAlerts용 락 키
    private static final Duration LOCK_TIMEOUT_UPDATE_QUEUE = Duration.ofMinutes(59); // updateSchedulerQueue용 타임아웃
    private static final Duration LOCK_TIMEOUT_PROCESS_ALERTS = Duration.ofMinutes(2); // processScheduledAlerts용 타임아웃

    @Autowired
    @Qualifier("16") // 수정된 RedisTemplate 사용
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate; // StringRedisTemplate 추가

    @Autowired
    public SchedulerAlertManager(SchedulerAlertRepository schedulerAlertRepository, RabbitTemplate rabbitTemplate) {
        this.schedulerAlertRepository = schedulerAlertRepository;
        this.rabbitTemplate = rabbitTemplate;
    }

    private boolean acquireLock(String lockKey, String uuid, Duration timeout) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        return Boolean.TRUE.equals(ops.setIfAbsent(lockKey, uuid, timeout)); // UUID로 락 획득 시도
    }

    private void releaseLock(String lockKey, String uuid) {
        ValueOperations<String, String> ops = stringRedisTemplate.opsForValue();
        String currentUuid = ops.get(lockKey);

        if (uuid.equals(currentUuid)) { // UUID가 일치할 때만 락 해제
            stringRedisTemplate.delete(lockKey);
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 * * * *") // 매 시간마다 실행
    public void updateSchedulerQueue() {
        String uuid = UUID.randomUUID().toString(); // 고유 UUID 생성
        if (!acquireLock(LOCK_KEY_UPDATE_QUEUE, uuid, LOCK_TIMEOUT_UPDATE_QUEUE)) {
            System.out.println("이미 다른 파드에서 실행 중");
            return; // 이미 다른 Pod에서 스케줄러가 실행 중이므로 종료
        }

        try {
            LocalDateTime now = LocalDateTime.now(); // 현재 시간
            LocalDateTime oneHourLater = now.plusHours(1); // 1시간 뒤까지

            // LocalTime으로 변환하여 사용
            LocalTime currentTime = now.toLocalTime();
            LocalTime timeOneHourLater = oneHourLater.toLocalTime();

            // 현재 시간과 1시간 뒤 사이의 예약된 알림들 가져오기
            List<ScheduleAlert> upcomingAlerts = schedulerAlertRepository.findByReserveTimeBetween(currentTime, timeOneHourLater);

            if (upcomingAlerts.isEmpty()) {
                System.out.println("현재 시간과 1시간 후 사이에 예약된 알림이 없습니다.");
                return; // 알림이 없을 경우 종료
            }

            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();

            // 알림이 존재할 경우 실행
            for (ScheduleAlert alert : upcomingAlerts) {
                LocalTime reserveTime = alert.getReserveTime();

                // LocalDate와 결합하여 LocalDateTime 생성 (필요한 날짜는 상황에 맞게 조정 가능)
                LocalDateTime alertTime = LocalDateTime.of(LocalDate.now(), reserveTime);

                // LocalDateTime을 Unix 타임스탬프로 변환하여 Redis Zset의 score로 사용
                double score = alertTime.toEpochSecond(ZoneOffset.UTC);

                // Redis Zset에 알림의 ID를 Long 타입으로 추가 (score가 같으면 덮어씌워짐)
                zSetOperations.add(SCHEDULED_ALERTS_ZSET_KEY, alert.getId(), score); // Long 타입으로 저장
            }
        } finally {
            releaseLock(LOCK_KEY_UPDATE_QUEUE, uuid); // 락 해제
        }
    }

    @Scheduled(cron = "0 * * * * *") // 매 1분마다 실행
    public void processScheduledAlerts() {
        String uuid = UUID.randomUUID().toString(); // 고유 UUID 생성
        if (!acquireLock(LOCK_KEY_PROCESS_ALERTS, uuid, LOCK_TIMEOUT_PROCESS_ALERTS)) {
            System.out.println("이미 다른 파드에서 실행 중");
            return; // 이미 다른 Pod에서 스케줄러가 실행 중이므로 종료
        }

        try {
            ZSetOperations<String, Object> zSetOperations = redisTemplate.opsForZSet();
            LocalDateTime now = LocalDateTime.now();
            double currentTimestamp = now.toEpochSecond(ZoneOffset.UTC);

            // Zset에서 현재 시간까지의 알림을 모두 가져옴
            Set<Object> alertIdsToProcess = zSetOperations.rangeByScore(SCHEDULED_ALERTS_ZSET_KEY, 0, currentTimestamp);

            assert alertIdsToProcess != null;
            for (Object alertIdObj : alertIdsToProcess) {
                // Redis에서 가져온 alertId를 Integer 또는 Long으로 변환
                Long alertId;
                if (alertIdObj instanceof Integer) {
                    alertId = ((Integer) alertIdObj).longValue(); // Integer를 Long으로 변환
                } else if (alertIdObj instanceof Long) {
                    alertId = (Long) alertIdObj; // 이미 Long일 경우 그대로 사용
                } else {
                    System.err.println("Unexpected alert ID type: " + alertIdObj.getClass().getName());
                    continue; // 처리할 수 없는 형식일 경우 다음으로 넘김
                }

                // 알림 ID로 DB에서 ScheduleAlert를 다시 조회
                ScheduleAlert alert = schedulerAlertRepository.findById(alertId).orElseThrow(
                        () -> new EntityNotFoundException("error"));

                if (alert != null) {
                    // DTO로 변환하여 전송
                    ScheduleAlertDto alertDto = new ScheduleAlertDto(
                            alert.getId(), alert.getReserveDay(), alert.getReserveTime(), alert.getScheduler().getId());
                    rabbitTemplate.convertAndSend(RabbitMqConfig.SCHEDULER_ALERT_QUEUE, alertDto);
//                    System.out.println("완료!");
                }
            }
        } finally {
            releaseLock(LOCK_KEY_PROCESS_ALERTS, uuid); // 락 해제
        }
    }
}
