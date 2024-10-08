package com.tweety.SwithT.lecture_apply.controller;

//@Slf4j
//@Component
//@RequiredArgsConstructor
//public class WaitingScheduler {
//
//    private final WaitingService waitingService;
//
//    // 1초마다
//    @Scheduled(fixedDelay = 1000)
//    private void eventScheduler() {
//        if (waitingService.validEnd()) {
//            log.info("===== 대기열이 종료되었습니다. =====");
//            return;
//        }
//        Long lectureGroupId = waitingService.getLectureGroupId();
//        if (lectureGroupId == null) {
//            log.warn("유효한 강의 그룹 ID가 없습니다.");
//            return;
//        }
//        //  대기열이 종료되지 않았으면 결제 처리 + 남은 대기열에 순번 표출
//        waitingService.processPayment(lectureGroupId);
//        waitingService.getOrder(lectureGroupId);
//    }
//}
