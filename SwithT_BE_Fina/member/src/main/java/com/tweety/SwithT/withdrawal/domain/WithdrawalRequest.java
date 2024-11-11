package com.tweety.SwithT.withdrawal.domain;

import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.member.domain.Member;
import com.tweety.SwithT.withdrawal.dto.WithdrawalResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class WithdrawalRequest extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    @CreationTimestamp
    private LocalDateTime requestTime;
    @Column(nullable = false)
    private Long amount;

    public WithdrawalResDto fromEntity() {
        return WithdrawalResDto.builder()
                .requestAmount(this.amount)
                .requestTime(this.requestTime)
                .description("정산금 출금")
                .build();

    }

}
