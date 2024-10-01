package com.tweety.SwithT.lecture_apply.service;

//@ExtendWith(MockitoExtension.class)
//class WaitingServiceTest {
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private ZSetOperations<String, Object> zSetOperations;
//
//    @InjectMocks
//    private WaitingService waitingService;
//
//    private final Long lectureGroupId = 3L;
//    private final int limitPeople = 100; // 제한 인원
//
//    public class User {
//        private Long memberId;
//        private String memberName;
//
//        public User(Long memberId, String memberName) {
//            this.memberId = memberId;
//            this.memberName = memberName;
//        }
//
//        public Long getMemberId() {
//            return memberId;
//        }
//
//        public String getMemberName() {
//            return memberName;
//        }
//    }
//
//    @BeforeEach
//    void setUp() {
//        waitingService.createQueue(lectureGroupId, limitPeople);
//        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
//    }
//
//    @Test
//    void 대기열에_500명_신청_테스트() {
//        // 500명의 사용자를 대기열에 추가
//        IntStream.range(0, 500).forEach(i -> {
//            String user = "User" + i;
//            Thread.currentThread().setName(user); // 스레드 이름 설정
//            waitingService.addQueue(lectureGroupId, (long) i);
//        });
//
//        // 대기열에 추가된 사용자 수를 모킹
//        when(zSetOperations.size(lectureGroupId.toString())).thenReturn((long) limitPeople);
//
//        // 대기열의 현재 사이즈를 확인
//        long currentSize = waitingService.getSize(lectureGroupId);
//        assertEquals(limitPeople, currentSize); // 대기열 사이즈는 최대 100명이어야 함
//    }
//
//    @Test
//    void 대기열_상태_조회() {
//
//        for (int i = 0; i < limitPeople; i++) {
//            new User((long) i, "USER" + 1);
//            waitingService.addQueue(lectureGroupId, (long) i);
//        }
//        waitingService.getOrder(lectureGroupId);
//
////        // Mocking을 통해 대기열에 사용자 추가된 상태
////        when(zSetOperations.range(lectureGroupId.toString(), 0, -1))
////                .thenReturn(IntStream.range(0, 100)
////                        .mapToObj(i -> "User" + i)
////                        .collect(Collectors.toSet()));
//
////        // 대기열 상태 조회
////        waitingService.getOrder(lectureGroupId, i);
//
//        // 확인을 위한 verify
//        verify(zSetOperations).range(lectureGroupId.toString(), 0, -1);
//    }
//
//    @Test
//    void 결제_처리(){
//        // Mocking을 통해 대기열에 이미 100명이 추가된 상태
//        when(zSetOperations.range(lectureGroupId.toString(), 0, limitPeople - 1))
//                .thenReturn(IntStream.range(0, limitPeople)
//                        .mapToObj(i -> "User" + i)
//                        .collect(Collectors.toSet()));
//
//        // Mocking 결제 후 대기열 사이즈
//        when(zSetOperations.size(lectureGroupId.toString())).thenReturn((long) limitPeople);
//
//        // 결제 처리
//        waitingService.processPayment(lectureGroupId);
//
//        // 대기열 사이즈 확인
//        when(zSetOperations.size(lectureGroupId.toString())).thenReturn(0L);
//        long sizeAfterPayment = waitingService.getSize(lectureGroupId);
//        assertEquals(0, sizeAfterPayment); // 결제 후 대기열은 비어 있어야 함
//    }
//
////    @Test
////    void 대기열_종료_검증() {
////        // 대기열에 사용자 추가
////        for (int i = 0; i < limitPeople; i++) {
////            waitingService.addQueue(lectureGroupId);
////        }
////        waitingService.processPayment(lectureGroupId); // 대기열 비워도 끝남
////
////        assertEquals(0, waitingService.getGroupLimit().getLimitPeople(), "Expected limitPeople to be 0");
////
////        // 대기열이 종료되었는지 검증
////        assertTrue(waitingService.validEnd());
////        System.out.println("Current limitPeople: " + waitingService.getGroupLimit().getLimitPeople());
////    }
//
//
//}
//
