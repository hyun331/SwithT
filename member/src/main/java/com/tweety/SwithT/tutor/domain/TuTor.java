package com.tweety.SwithT.tutor.domain;
import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.common.domain.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class TuTor extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime birthday;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = true)
    private String education;

    @Column(nullable = false, unique = true)
    private String nickName;

    @Column(precision = 2, scale = 1, nullable = false)
    private BigDecimal avgScore = BigDecimal.valueOf(0.0);

    Long availableMoney = 0L;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role gender = Role.MAN;


}
