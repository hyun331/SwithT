package com.tweety.SwithT.lecture_apply.service;

import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import static org.mockito.Mockito.when;

public class LectureApplyServiceTest {

    @InjectMocks
    private LectureApplyService lectureApplyService;

    @Mock
    private WaitingService waitingService;

    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    @Mock
    private ZSetOperations<String, Object> zSetOperations;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // RedisTemplate의 opsForZSet 메서드가 ZSetOperations를 반환하도록 설정
        when(redisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

//    @Test
//    public void testApplyForLecture_When120TuteesApply_ShouldProcessCorrectly() {
//        Long lectureGroupId = 1L; // 강의 그룹 ID
//        Long[] memberIds = new Long[120];
//
//        // 120명의 회원 ID 초기화
//        for (int i = 0; i < 120; i++) {
//            memberIds[i] = (long) (i + 1); // 1부터 120까지
//        }
//
//        // 강의 신청 DTO
//        LectureApplySavedDto dto = new LectureApplySavedDto();
//        dto.setLectureGroupId(lectureGroupId);
//
//        // 강의 그룹을 존재하는 것으로 설정
//        when(zSetOperations.score(any(), any())).thenReturn(null); // 대기열이 비어있다고 가정
//        when(waitingService.applyForLecture(any(), any())).thenCallRealMethod();
//
//        for (Long memberId : memberIds) {
//            String memberName = "USER" + memberId;
//            // 강의 신청 호출
//            lectureApplyService.tuteeLectureApply(dto);
//        }
//
//        // 대기열에 추가되었는지 확인
//        for (Long memberId : memberIds) {
//            verify(waitingService, times(1)).applyForLecture(lectureGroupId, memberId);
//        }
//    }
}
