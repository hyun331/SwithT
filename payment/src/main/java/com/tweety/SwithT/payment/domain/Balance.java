package com.tweety.SwithT.payment.domain;

import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.payment.dto.BalanceResDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime balancedTime;

    @Column(nullable = false)
    private Long cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "enum('ADMIT', 'CANCELED', 'STANDBY') default 'STANDBY'")
    private Status status;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payments payments;
    
    private Long memberId;

    public Balance changeStatus(Status status) {
        this.status = status;
        return this;
    }

    public BalanceResDto fromEntity(){
        String paymentName = this.payments.getName();
        return BalanceResDto.builder()
                .incomeAmount(this.cost)
                .createdTime(this.balancedTime)
                .description(paymentName)
                .build();
    }
}