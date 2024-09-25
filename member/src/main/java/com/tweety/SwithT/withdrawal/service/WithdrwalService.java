package com.tweety.SwithT.withdrawal.service;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.member.dto.MemberInfoResDto;
import com.tweety.SwithT.member.repository.MemberRepository;
import com.tweety.SwithT.member.service.MemberService;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import com.tweety.SwithT.withdrawal.dto.WithdrwalReqDto;
import com.tweety.SwithT.withdrawal.repository.WithdrwalRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class WithdrwalService {

    private final WithdrwalRepository withdrwalRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;


    @Autowired
    public WithdrwalService(WithdrwalRepository withdrwalRepository, MemberRepository memberRepository, MemberService memberService) {
        this.withdrwalRepository = withdrwalRepository;
        this.memberRepository = memberRepository;
        this.memberService = memberService;
    }

    public void WithdrwalRequest(WithdrwalReqDto dto) {

        String id = SecurityContextHolder.getContext().getAuthentication().getName();
        System.out.println("String id : "+id);
        Member member = memberRepository.findById(Long.valueOf(id))
                .orElseThrow(() -> new SecurityException("인증되지 않은 사용자입니다."));
        System.out.println("member id :"+member.getId());

        if (member.getAvailableMoney() < dto.getAmount()) {
            throw new IllegalStateException("출금 가능 금액이 부족합니다.");
        } else if (member.getAvailableMoney() < 0)  {
            throw new IllegalStateException("출금 가능 금액이 부족합니다.");
        }

        memberService.subtraction(dto.getAmount());
        WithdrawalRequest withdrawalRequest = dto.toEntity(member);
        withdrwalRepository.save(withdrawalRequest);

    }

}
