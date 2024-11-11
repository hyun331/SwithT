package com.tweety.SwithT.member.dto;

import com.tweety.SwithT.member.domain.Gender;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TutorInfoResDto {
    private String name;
    private Gender gender;
    private int age;
    private BigDecimal avgScore;
    private String introduce;

}