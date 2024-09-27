package com.tweety.SwithT.member.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tweety.SwithT.common.domain.BaseTimeEntity;
import com.tweety.SwithT.member.dto.MemberInfoResDto;
import com.tweety.SwithT.member.dto.MemberUpdateDto;
import com.tweety.SwithT.review.domain.Review;
import com.tweety.SwithT.scheduler.domain.Scheduler;
import com.tweety.SwithT.withdrawal.domain.WithdrawalRequest;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"}) // Hibernate 프록시 무시
public class Member extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    @Builder.Default
    @JsonBackReference
    private List<Scheduler> schedulers = new ArrayList<>();

    @OneToMany(mappedBy = "member", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<WithdrawalRequest> withdrawalRequests = new ArrayList<>();

    @OneToMany(mappedBy = "writerId", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Review> reviews = new ArrayList<>();

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = true)
    private String password;

    @Column(nullable = true) // 동명이인 고려
    private String name;

    @Column(nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthday;

    @Column(nullable = false)
    private String phoneNumber;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String profileImage;

    @Column(nullable = true)
    private String introduce;

    @Column(nullable = true)
    private String education;

    @Builder.Default
    @Column(precision = 2, scale = 1, nullable = true)
    private BigDecimal avgScore = BigDecimal.valueOf(0.0);

    @Builder.Default
    @Column(nullable = true)
    private Long availableMoney = 1000000L;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Gender gender = Gender.MAN;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false)
    private Role role = Role.TUTEE;

    // 내 정보 데이터 FromEntity 메서드
    public MemberInfoResDto infoFromEntity(){
        return MemberInfoResDto.builder()
                .profileImage(this.profileImage)
                .name(this.name)
                .birthday(this.birthday)
                .gender(this.gender)
                .address(this.address)
                .email(this.email)
                .phoneNumber(this.phoneNumber)
                .education(this.education)
                .introduce(this.introduce)
                .build();
    }

    public Member infoUpdate(MemberUpdateDto dto) {
        this.name = dto.getName();
        this.birthday = dto.getBirthday();
        this.gender = Gender.valueOf(dto.getGender());
        this.address = dto.getAddress();
        this.phoneNumber = dto.getPhoneNumber();
        this.education = dto.getEducation();
        this.introduce = dto.getIntroduce();
        return this;
    }

    public Member imageUpdate(String imgUrl){
        this.profileImage = imgUrl;
        return this;
    }

    public void balanceUpdate(Long amount) {
        this.availableMoney -= amount;
    }
}
