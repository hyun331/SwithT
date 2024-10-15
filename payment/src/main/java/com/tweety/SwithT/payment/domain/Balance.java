package com.tweety.SwithT.payment.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime balancedTime;

    @Column(nullable = false)
    private Long cost;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @ColumnDefault("Standby")
    private Status status;

    @OneToOne
    @JoinColumn(name = "payment_id", nullable = false)
    private Payments payments;

    private Long memberId;

    public Balance changeStatus(){
        this.status = Status.ADMIT;
        return this;
    }
}
