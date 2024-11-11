package com.tweety.SwithT.withdrawal.dto;

import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WithdrawalReqDto {

    private Long memberId;
    @Nullable
    @CreationTimestamp
    private LocalDateTime requestTime;
    private Long amount;

    public WithdrawalRequest toEntity(Member member){

        System.out.println(member.getId()+"+"+requestTime+" "+amount);

        return WithdrawalRequest.builder()
                .member(member)
                .requestTime(this.requestTime)
                .amount(this.amount)
                .build();

    }

}
