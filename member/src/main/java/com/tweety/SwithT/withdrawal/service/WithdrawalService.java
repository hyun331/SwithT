package com.tweety.SwithT.withdrawal.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import com.tweety.SwithT.member.service.MemberService;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import com.tweety.SwithT.withdrawal.dto.WithdrawalReqDto;
import com.tweety.SwithT.withdrawal.dto.WithdrawalResDto;
import com.tweety.SwithT.withdrawal.repository.WithdrawalRepository;
import com.twilio.rest.api.v2010.account.Balance;
import jakarta.persistence.EntityNotFoundException;
import lombok.With;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class WithdrawalService {

    private final WithdrawalRepository withdrawalRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    @Autowired
    public WithdrawalService(WithdrawalRepository withdrawalRepository, MemberRepository memberRepository, MemberService memberService) {
        this.withdrawalRepository = withdrawalRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    public Page<WithdrawalResDto> getRequestList(Pageable pageable){

        Member tutor = memberRepository
                .findById(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보입니다."));

        // Pageable에 requestTime으로 정렬 추가
        Pageable sortedByRequestTime = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("requestTime").descending());

        // Pageable을 사용하여 requestTime 기준으로 정렬된 WithdrawalRequest 리스트 가져오기
        Page<WithdrawalRequest> withdrawalRequests = withdrawalRepository.findByMember(tutor, sortedByRequestTime);

        // Page<WithdrawalRequest>를 Page<WithdrawalResDto>로 변환하여 반환
        return withdrawalRequests.map(WithdrawalRequest::fromEntity);
    }

    public void WithdrwalRequest(WithdrawalReqDto dto) {

        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));

        if (member.getAvailableMoney() < dto.getAmount()) {
            throw new IllegalStateException("출금 가능 금액이 부족합니다.");
        } else if (member.getAvailableMoney() < 0)  {
            throw new IllegalStateException("출금 가능 금액이 부족합니다.");
        }

        memberService.subtraction(dto.getAmount());
        WithdrawalRequest withdrawalRequest = dto.toEntity(member);
        withdrawalRepository.save(withdrawalRequest);

    }
    public Map<String, Object> getChartList(int months) {
        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        Member member = memberRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));

        // 현재 날짜와 months 기간 전 날짜 구하기
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(months);

        // memberId를 기준으로 해당 기간 내의 WithdrawalRequest 리스트 가져오기 (삭제되지 않은 항목만)
        List<WithdrawalRequest> withdrawalRequestList = withdrawalRepository.findByMemberIdAndDelYnAndRequestTimeBetween(
                member.getId(), "N", startDate, now
        );

        // requestTime을 "yyyy/MM" 형식으로 변환하여 그룹화
        Map<String, Long> withdrawalByMonth = withdrawalRequestList.stream()
                .collect(Collectors.groupingBy(
                        withdrawal -> withdrawal.getRequestTime().format(DateTimeFormatter.ofPattern("yyyy/MM")),
                        Collectors.summingLong(WithdrawalRequest::getAmount)
                ));

        // 기간 동안의 모든 달을 포함하는 labels 초기화
        List<String> labels = new ArrayList<>();
        for (int i = 0; i <= months; i++) {
            String monthLabel = startDate.plusMonths(i).format(DateTimeFormatter.ofPattern("yyyy/MM"));
            labels.add(monthLabel);
        }

        // dataset (각 달의 합산 금액, 없으면 0으로 채움)
        List<Long> dataset = labels.stream()
                .map(label -> withdrawalByMonth.getOrDefault(label, 0L))
                .collect(Collectors.toList());

        // Chart.js에서 사용할 데이터 구조로 변환
        return Map.of(
                "labels", labels,
                "data", dataset
        );
    }

}
