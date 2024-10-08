package com.tweety.SwithT.lecture_apply.service;

import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class LectureApplyServiceTest {
//
//    @InjectMocks
//    private WaitingService waitingService;
//
//    @InjectMocks
//    private LectureApplyService lectureApplyService;
//
//    @Mock
//    private LectureGroupRepository lectureGroupRepository;
//
//    @Mock
//    private LectureApplyRepository lectureApplyRepository;
//
//    private final int lectureGroupLimit = 100;  // 강의 그룹 제한 인원 수
//    LectureGroup mockGroup = new LectureGroup();
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        // 제한 인원이 100인 강의 그룹을 설정
//        mockGroup.setId(3L);
//        mockGroup.setLimitPeople(lectureGroupLimit);
//        when(lectureGroupRepository.findByIdAndDelYn(3L, "N")).thenReturn(Optional.of(mockGroup));
//    }
//
//    @Test
//    public void testWaitingQueueWith300Applicants() throws InterruptedException {
//        ExecutorService executor = Executors.newFixedThreadPool(300);
//
//        for (long i = 1; i <= 300; i++) {
//            long memberId = i;
//            String memberName = "USER" + i;
//            executor.submit(() -> {
//                try {
//                    LectureApplySavedDto dto = new LectureApplySavedDto();
//                    dto.setLectureGroupId(1L);
//                    // 강의 신청 시뮬레이션
//                    String result = lectureApplyService.testTuteeLectureApply(dto, mockGroup, memberId, memberName, mockGroup.getId(), lectureGroupLimit);
//                    System.out.println(result);
//                } catch (Exception e) {
//                    System.err.println("회원 ID: " + memberId + " 오류 - " + e.getMessage());
//                }
//            });
//        }
//        executor.shutdown();
//        while (!executor.isTerminated()) {
//            // 모든 스레드가 종료될 때까지 대기
//        }
//    }
}

