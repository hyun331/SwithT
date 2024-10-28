package com.tweety.SwithT.withdrawal.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.repository.MemberRepository;
import com.tweety.SwithT.member.service.MemberService;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import com.tweety.SwithT.withdrawal.dto.WithdrawalReqDto;
import com.tweety.SwithT.withdrawal.dto.WithdrawalResDto;
import com.tweety.SwithT.withdrawal.repository.WithdrawalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
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

    public List<WithdrawalResDto> getRequestList(){

        Member tutorId = memberRepository
                .findById(Long.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원 정보 입니다."));

        List<WithdrawalRequest> withdrawalRequests = withdrawalRepository.findByMember(tutorId);

        return withdrawalRequests
                .stream()
                .map(WithdrawalRequest::fromEntity)
                .collect(Collectors.toList());
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

}
