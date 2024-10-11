package com.tweety.SwithT.lecture_apply.service;
//
//import com.tweety.SwithT.common.domain.Status;
//import com.tweety.SwithT.common.service.RedisStreamProducer;
//import com.tweety.SwithT.lecture.domain.LectureApply;
//import com.tweety.SwithT.lecture.domain.LectureGroup;
//import com.tweety.SwithT.lecture.repository.LectureGroupRepository;
//import com.tweety.SwithT.lecture.repository.LectureRepository;
//import com.tweety.SwithT.lecture_apply.dto.LectureApplyAfterResDto;
//import com.tweety.SwithT.lecture_apply.repository.LectureApplyRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.ArgumentCaptor;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.data.redis.core.RedisTemplate;
//
//import java.util.HashSet;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//public class WaitingServiceTest {
//
//    private WaitingService waitingService;
//
//    @Mock
//    private RedisTemplate<String, Object> redisTemplate;
//
//    @Mock
//    private LectureGroupRepository lectureGroupRepository;
//
//    @Mock
//    private LectureApplyRepository lectureApplyRepository; // 추가된 부분
//
//    @Mock
//    private LectureApplyService lectureApplyService;
//
//    @Mock
//    private RedisStreamProducer redisStreamProducer;
//
//    private LectureGroup lectureGroup;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        // LectureApplyRepository를 추가하여 WaitingService 초기화
//        waitingService = new WaitingService(redisTemplate, lectureGroupRepository, lectureApplyRepository, redisStreamProducer);
//
//        // 기본 강의 그룹 설정
//        lectureGroup = new LectureGroup();
//        lectureGroup.setId(1L);
//        lectureGroup.setRemaining(5);
//        lectureGroup.setIsAvailable("Y");
//    }
//
//    @Test
//    public void testAddQueue_UserAddedSuccessfully() {
//        Long memberId = 100L;
//        Long lectureGroupId = lectureGroup.getId();
//
//        // Redis에서 해당 멤버가 대기열에 없음을 시뮬레이션
//        when(redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId)).thenReturn(null);
//
//        String response = waitingService.addQueue(lectureGroupId, memberId);
//
//        assertEquals("Successfully created & applied for the lecture.", response);
//        verify(redisTemplate.opsForZSet()).add(lectureGroupId.toString(), memberId, anyInt());
//    }
//
//    @Test
//    public void testAddQueue_UserAlreadyInQueue() {
//        Long memberId = 100L;
//        Long lectureGroupId = lectureGroup.getId();
//
//        // Redis에서 해당 멤버가 대기열에 이미 있음을 시뮬레이션
//        when(redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId)).thenReturn(1.0);
//
//        String response = waitingService.addQueue(lectureGroupId, memberId);
//
//        assertEquals("User is already in the queue.", response);
//        verify(redisTemplate.opsForZSet(), never()).add(anyString(), any(), anyInt());
//    }
//
//    @Test
//    public void testGetOrder() {
//        Long memberId = 100L;
//        String queueKey = lectureGroup.getId().toString();
//
//        Set<Object> queue = new HashSet<>();
//        queue.add(memberId);
//
//        // 대기열을 설정
//        when(redisTemplate.opsForZSet().range(queueKey, 0, -1)).thenReturn(queue);
//        when(redisTemplate.opsForZSet().rank(queueKey, memberId)).thenReturn(0L);
//
//        waitingService.getOrder(memberId.toString(), queueKey);
//
//        // 대기열 상태 확인 메서드 호출 확인
//        verify(redisStreamProducer).publishWaitingMessage(any(), any(), any(), any());
//    }
//
//    @Test
//    public void testGetQueueSize() {
//        Long lectureGroupId = lectureGroup.getId();
//        when(redisTemplate.opsForZSet().size(lectureGroupId.toString())).thenReturn(10L);
//
//        long queueSize = waitingService.getQueueSize(lectureGroupId);
//
//        assertEquals(10L, queueSize);
//    }
//
//    @Test
//    public void testTuteeLectureApply_StatusSTANDBY() throws InterruptedException {
//        Long memberId = 100L;
//        Long lectureGroupId = lectureGroup.getId();
//
//        // 강의 그룹이 사용 가능함을 설정
//        when(lectureGroupRepository.findByIdAndDelYn(lectureGroupId, "N")).thenReturn(Optional.of(lectureGroup));
//
//        // 대기열에 추가된 멤버가 없음을 시뮬레이션
//        when(redisTemplate.opsForZSet().score(lectureGroupId.toString(), memberId)).thenReturn(null);
//
//        // 대기열에 추가하고, 대기열 확인
//        waitingService.addQueue(lectureGroupId, memberId);
//
//        // 대기열에서 자신의 순서가 0이 되도록 시뮬레이션
//        when(redisTemplate.opsForZSet().rank(lectureGroupId.toString(), memberId)).thenReturn(0L);
//        when(redisTemplate.opsForZSet().size(lectureGroupId.toString())).thenReturn(1L);
//
//        // 신청 상태를 저장하는 로직 실행
//        LectureApplyAfterResDto result = lectureApplyService.tuteeLectureApply(lectureGroupId, memberId, "testUser");
//
//        // 강의 신청 상태가 STANDBY로 저장되었는지 확인
//        ArgumentCaptor<LectureApply> lectureApplyCaptor = ArgumentCaptor.forClass(LectureApply.class);
//        verify(lectureApplyRepository).save(lectureApplyCaptor.capture());
//        assertEquals(Status.STANDBY, lectureApplyCaptor.getValue().getStatus());
//    }
//}
//
