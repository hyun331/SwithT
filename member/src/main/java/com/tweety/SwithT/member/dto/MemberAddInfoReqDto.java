package com.tweety.SwithT.member.dto;

import com.tweety.SwithT.member.domain.Gender;
import com.tweety.SwithT.member.domain.Role;
import jakarta.annotation.Nullable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberAddInfoReqDto {


    @NotEmpty(message = "이름은 필수 작성 항목 입니다.")
    private String name;
    @NotEmpty(message = "이메일은 필수 작성 항목 입니다.")
    @Email(message = "이메일 형식이 유효하지 않습니다.")
    private String email;
//    테스트 용이성을 위해 주석처리 나중에 살리겠음
//    @NotEmpty(message = "Password is required")
//    @Pattern(regexp = ".*[!@#$%^&*(),.?\":{}|<>].*", message = "패스워드는 특수 문자를 포함한 8자리 이상 입니다.")
//    @Size(min = 8, message = "비밀번호는 8자리 이상이어야합니다")
    @NotNull
    private LocalDate birthday;
//    테스트 용이성을 위해 주석처리 나중에 살리겠음
//    @NotEmpty(message = "휴대폰 번호는 필수 작성 항목 입니다.")
//    @Pattern(regexp = "^[0-9]{10,11}$", message = "휴대폰 번호 형식이 유효하지 않습니다.")
    @NotNull
    private String phoneNumber;
    @Nullable
    private String address;
    @Nullable
    private String profileImage;

    @Nullable
    private String introduce;

    @Nullable
    private String education;

    @Builder.Default
    @Column(precision = 2, scale = 1, nullable = true)
    private BigDecimal avgScore = BigDecimal.valueOf(0.0);

    //  enum 타입에는 @NotEmpty 적용할 수 없음. 적용 시 에러 발생.
//    @NotEmpty(message = "성별은 필수 항목 입니다.")
    private Gender gender;

    private Role role;

}
